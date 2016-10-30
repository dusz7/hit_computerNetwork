package com.dusz7.client;

import com.dusz7.just4easy.Easy;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;


/**
 * Created by dusz2 on 2016/10/25 0025.
 */
public class GBNClientThread extends Thread {

    private static int winSize;
    private static int seqNum ;
    private static int begin = 0, end;
    private static int dataNum;
    private static int toSendNum;
//    private static Timer[] timers;
    static Timer timer;

    private static InetAddress inetAddress;

    private static DatagramSocket clientSocket;
    private byte[] receive = new byte[1024];
    private byte[] send = new byte[1024];


    public GBNClientThread(){

        try {
            inetAddress = InetAddress.getByName("localhost");
        }catch (UnknownHostException e){

        }

        winSize = Easy.WIN_SIZE;
        end = begin + winSize -1;
        dataNum = Easy.DATA_NUM;
        toSendNum = dataNum;
        seqNum = 50;
//        timers = new Timer[50];

        try {
            clientSocket = new DatagramSocket();
        }catch (SocketException e){

        }

        System.out.println("丢包概率为0.3,在服务器端设定");
        System.out.println("重传定时器为3秒,在客户端设定,逾期则GoBack重新发送");
        System.out.println("滑动窗大小为" + winSize);
        System.out.println("客户端即将发送" + dataNum +"个数据包");

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
            DatagramPacket sendPacket = new DatagramPacket(send,send.length,inetAddress, Easy.GBNSP);
            try {
                clientSocket.send(sendPacket);
                toSendNum--;
                //设置定时器，设置时间为3秒
//                timers[i] = new Timer(3000, new DelayActionListener(clientSocket,i,timers));
//                timers[i].start();
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

                //关闭定时器
//                timers[ackNum].stop();


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

                    DatagramPacket sendPacket = new DatagramPacket(send,send.length,inetAddress,Easy.GBNSP);
                    try {
                        clientSocket.send(sendPacket);
                        toSendNum--;

                        //设置定时器
//                        timers[end] = new Timer(3000, new DelayActionListener(clientSocket,end,timers));
//                        timers[end].start();
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

    public static int getEnd() {
        return end;
    }
}

//关于计时器的设置
class DelayActionListener implements ActionListener{

    private DatagramSocket socket;
    private int seqNo;
//    private Timer[] timers;


    public DelayActionListener(DatagramSocket clientSocket, int seqNum, Timer[] timers){
        this.socket = clientSocket;
        this.seqNo = seqNum;
//        this.timers = timers;
    }

    public DelayActionListener(DatagramSocket clientSocket, int seqNo){
        this.socket = clientSocket;
        this.seqNo = seqNo;
    }

    @Override
    public void actionPerformed(ActionEvent e){

        GBNClientThread.timer.stop();
        GBNClientThread.timer = new Timer(3000,new DelayActionListener(socket,seqNo));
        GBNClientThread.timer.start();

        int end = seqNo+Easy.WIN_SIZE-1;
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
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, Easy.GBNSP);
                socket.send(sendPacket);
                System.out.println("-------  Client发送数据包 " + i +"  -------");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

//        int end = GBNClientThread.getEnd();
//        System.out.println("-------  客户端准备重传数据 " + seqNo +"--" + end+"  -------");
//        //强行来
//        for (int i = seqNo; i <= end; i++){
//            timers[i].stop();
//            timers[i].start();
//        }
//        for (int i = seqNo; i <= end; i++){
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
//                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, Easy.GBNSP);
//                socket.send(sendPacket);
//                System.out.println("-------  客户端发送数据包 " + i+"  -------");
//            } catch (Exception e1) {
//                e1.printStackTrace();
//            }
////            timers[i].stop();
////            timers[i].start();
//        }
    }
}
