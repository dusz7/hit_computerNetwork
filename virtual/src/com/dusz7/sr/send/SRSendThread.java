package com.dusz7.sr.send;

import com.dusz7.just4easy.Easy;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;

/**
 * Created by dusz2 on 2016/10/31 0031.
 */
public class SRSendThread extends Thread {

    private static int winSize;
    private static int seqNum ;
    private static int begin = 0, end;

    private static int[] getACK;

    private static int dataNum;
    private static int toSendNum;
    static int serverPort;
    static Timer[] timers;

    private static InetAddress inetAddress;

    private static DatagramSocket clientSocket;
    private byte[] receive = new byte[1024];
    private byte[] send = new byte[1024];


    public SRSendThread(int port){

        try {
            inetAddress = InetAddress.getByName("localhost");
        }catch (UnknownHostException e){

        }

        serverPort = port;
        winSize = Easy.SR_S_WIN_ZIZE;
        end = begin + winSize -1;
        dataNum = Easy.SR_DATA_NUM;
        toSendNum = dataNum;
        seqNum = Easy.SEQ_NUM;


        timers = new Timer[seqNum];
        getACK = new int[seqNum];

        for(int i = 0; i < getACK.length; i++){
            getACK[i] = 0;
        }

        try {
            clientSocket = new DatagramSocket();
        }catch (SocketException e){

        }

        System.out.println("Client即将发送" + dataNum +"个数据包");

        //设置计时器，定时为3秒
//        timer = new Timer(3000,new DelayActionListener(clientSocket,begin));
//        timer.start();

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

                //设置定时器，设置时间为3秒
                timers[i] = new Timer(3000, new DelayActionListener(clientSocket,i,timers));
                timers[i].start();

                System.out.println("-------  Client发送数据包："+i+"  -------");
            }catch (IOException e){

            }
        }
    }

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

                //关闭定时器
                timers[ackNum].stop();

                if(ackNum == dataNum -1){
                    System.out.println("-------  Client数据全部发送完毕!" +"  -------");
                    return;
                }
                else if(ackNum > begin){

                    getACK[ackNum] = 1;
                }
                else if(ackNum == begin && toSendNum > 0){

                    int moveNum = 0;
                    getACK[begin] = 1;

//                    begin++;
//                    end++;
//
//                    //窗口移动1
//                    if(end <10 ){
//                        send = (new String(end+"x "+"seq")).getBytes();
//                    }
//                    else if(end < 100){
//                        send = (new String(end+" "+"seq")).getBytes();
//                    }
//                    DatagramPacket sendPacket = new DatagramPacket(send,send.length,inetAddress,serverPort);
//                    try {
//                        clientSocket.send(sendPacket);
//                        toSendNum--;
//
//                        //设置定时器
//                        timers[end] = new Timer(3000, new DelayActionListener(clientSocket,end,timers));
//                        timers[end].start();
//                        System.out.println("-------  Client发送数据包："+end+"  -------");
//
//                        System.out.println("-------  Client发送数据包："+end+"  -------");
//                    }catch (IOException e){
//
//                    }

                    //移动窗口
                    for (int i = begin; i <= end; i++){
                        if(getACK[i] == 1) {
                            moveNum++;
                            getACK[i] = 0;

                            int next = i - begin + 1 + end;

                            if (next < 10) {
                                send = (new String(next + "x " + "seq")).getBytes();
                            } else if (next < 100) {
                                send = (new String(next + " " + "seq")).getBytes();
                            }
                            DatagramPacket sendPacket = new DatagramPacket(send, send.length, inetAddress, serverPort);
                            try {
                                clientSocket.send(sendPacket);
                                toSendNum--;

                                //设置定时器
                                timers[next] = new Timer(3000, new DelayActionListener(clientSocket, next, timers));
                                timers[next].start();

                                System.out.println("-------  Client发送数据包：" + next + "  -------");
                            } catch (IOException e) {
                            }

                        }else {
                            break;
                        }
                    }

                    begin+= moveNum;
                    end+= moveNum;

                    //巡回---没有检验正确性，估计是没啥用
                    if(end >seqNum - 1){
                        end = 0;
                    }
                    if(begin >seqNum - 1){
                        begin = 0;
                    }


                }

            }catch (IOException e){

            }
        }//end of while
    }

}

//关于计时器的设置
class DelayActionListener implements ActionListener{

    private DatagramSocket socket;
    private int seqNo;
    private Timer[] timers;


    public DelayActionListener(DatagramSocket clientSocket, int seqNo, Timer[] timers){
        this.socket = clientSocket;
        this.seqNo = seqNo;
        this.timers = timers;
    }

    @Override
    public void actionPerformed(ActionEvent e){

        timers[seqNo].stop();
        System.out.println("-------  Client准备重传数据 " + seqNo +"  -------");

        byte[] sendDate = null;
        InetAddress serverAdress = null;
        try {
            serverAdress = InetAddress.getByName("localhost");
            if(seqNo < 10){
                sendDate = (new String(seqNo+"x "+"seq").getBytes());
            }
            else if(seqNo < 100){
                sendDate = (new String(seqNo+" "+"seq").getBytes());
            }

            DatagramPacket sendPacket = new DatagramPacket(sendDate,sendDate.length,serverAdress, SRSendThread.serverPort);
            socket.send(sendPacket);

            timers[seqNo].start();

            System.out.println("-------  Client重发数据包 "+seqNo+"  -------");
        }catch (Exception e1){
            e1.printStackTrace();
        }


    }
}


////关于计时器的设置
//class DelayActionListener1 implements ActionListener {
//
//    private DatagramSocket socket;
//    private int seqNo;
//
//    public DelayActionListener1(DatagramSocket clientSocket, int seqNo){
//        this.socket = clientSocket;
//        this.seqNo = seqNo;
//    }
//
//    @Override
//    public void actionPerformed(ActionEvent e){
//
//        SRSendThread.timer.stop();
//        SRSendThread.timer = new Timer(3000,DelayActionListener1(socket,seqNo));
//        SRSendThread.timer.start();
//
//        int end = seqNo+Easy.GBN_WIN_SIZE -1;
//        System.out.println("-------  Client准备重传数据 " + seqNo +"--" + end +"  -------");
//
//        for(int i = seqNo; i <= end; i++){
//            byte[] sendData = null;
//            InetAddress serverAddress = null;
//            try {
//                serverAddress = InetAddress.getByName("localhost");
//                if(i <10 ){
//                    sendData = (new String(i+"x "+"seq")).getBytes();
//                }
//                else if(i < 100){
//                    sendData = (new String(i+" "+"seq")).getBytes();
//                }
//                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SRSendThread.serverPort);
//                socket.send(sendPacket);
//                System.out.println("-------  Client发送数据包 " + i +"  -------");
//            } catch (Exception e1) {
//                e1.printStackTrace();
//            }
//        }
//
//    }
//}
