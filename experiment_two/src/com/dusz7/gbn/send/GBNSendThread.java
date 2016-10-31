package com.dusz7.gbn.send;

import com.dusz7.just4easy.Easy;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;


/**
 * Created by dusz2 on 2016/10/25 0025.
 */
public class GBNSendThread extends Thread {

    private static int winSize;
    private static int seqNum ;
    private static int begin = 0, end;
    private static int dataNum;
    private static int toSendNum;
    static int serverPort;

    static Timer timer;

    private static InetAddress inetAddress;

    private static DatagramSocket clientSocket;
    private byte[] receive = new byte[1024];
    private byte[] send = new byte[1024];


    public GBNSendThread(int port){

        try {
            inetAddress = InetAddress.getByName("localhost");
        }catch (UnknownHostException e){

        }

        serverPort = port;
        winSize = Easy.GBN_WIN_SIZE;
        end = begin + winSize -1;
        dataNum = Easy.DATA_NUM;
        toSendNum = dataNum;
        seqNum = Easy.GBN_SEQ_NUM;

        try {
            clientSocket = new DatagramSocket();
        }catch (SocketException e){

        }

        System.out.println("Client即将发送" + dataNum +"个数据包");

        //设置计时器，定时为3秒
        timer = new Timer(3000,new DelayActionListener(clientSocket,begin));
        timer.start();

        //首先发送窗格大小个数的数据包
        for (int i = begin; i <= end; i++){
            if(i <10 ){
                send = (new String(i+"x "+"seq")).getBytes();
            }
            else if(i < 100){
                send = (new String(i+" "+"seq")).getBytes();
            }
            DatagramPacket sendPacket = new DatagramPacket(send,send.length,inetAddress, serverPort);
            try {
                clientSocket.send(sendPacket);
                toSendNum--;

                System.out.println("-------  Client发送数据包："+i+"  -------");
            }catch (IOException e){

            }
        }
    }

    @Override
    public void run(){
        while (true){
            DatagramPacket receivePacket = new DatagramPacket(receive,receive.length);
            try{
                clientSocket.receive(receivePacket);
                int ackNum = -1;

                if(receive[4] == 'x'){
                    ackNum = receive[3]-'0';
                }
                else {
                    ackNum = (receive[3]-'0')*10 + (receive[4]-'0');
                }
                System.out.println("-------  Client接收到ACK序号："+ackNum+"  -------");

                if(ackNum == dataNum -1){
                    System.out.println("-------  Client数据全部发送完毕!" +"  -------");
                    return;
                }
                else if(ackNum == begin && toSendNum > 0){

                    timer.stop();
                    begin++;
                    end++;
                    //巡回
                    if(end >seqNum - 1){
                        end = 0;
                    }
                    if(begin >seqNum - 1){
                        begin = 0;
                    }

                    if(end <10 ){
                        send = (new String(end+"x "+"seq")).getBytes();
                    }
                    else if(end < 100){
                        send = (new String(end+" "+"seq")).getBytes();
                    }

                    DatagramPacket sendPacket = new DatagramPacket(send,send.length,inetAddress,serverPort);
                    try {
                        clientSocket.send(sendPacket);
                        toSendNum--;

                        //设置定时器
                        timer = new Timer(3000,new DelayActionListener(clientSocket,begin));
                        timer.start();

                        System.out.println("-------  Client发送数据包："+end+"  -------");
                    }catch (IOException e){

                    }

                }

            }catch (IOException e){

            }
        }//end of while

    }//end of run

}

//关于计时器的设置
class DelayActionListener implements ActionListener{

    private DatagramSocket socket;
    private int seqNo;

    public DelayActionListener(DatagramSocket clientSocket, int seqNo){
        this.socket = clientSocket;
        this.seqNo = seqNo;
    }

    @Override
    public void actionPerformed(ActionEvent e){

        GBNSendThread.timer.stop();
        GBNSendThread.timer = new Timer(3000,new DelayActionListener(socket,seqNo));
        GBNSendThread.timer.start();

        int end = seqNo+Easy.GBN_WIN_SIZE -1;
        System.out.println("-------  Client准备重传数据 " + seqNo +"--" + end +"  -------");

        for(int i = seqNo; i <= end; i++){
            byte[] sendData = null;
            InetAddress serverAddress = null;
            try {
                serverAddress = InetAddress.getByName("localhost");
                if(i <10 ){
                    sendData = (new String(i+"x "+"seq")).getBytes();
                }
                else if(i < 100){
                    sendData = (new String(i+" "+"seq")).getBytes();
                }
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, GBNSendThread.serverPort);
                socket.send(sendPacket);
                System.out.println("-------  Client发送数据包 " + i +"  -------");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

    }
}
