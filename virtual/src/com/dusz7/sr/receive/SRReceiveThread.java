package com.dusz7.sr.receive;

import com.dusz7.just4easy.Easy;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by dusz2 on 2016/10/31 0031.
 */
public class SRReceiveThread extends Thread {

    private static int serverPort;
    private static DatagramPacket receivePacket;
    private static DatagramPacket sendPacket;
    private static DatagramSocket serverSocket;

    private static byte[] receive = new byte[1024];
    private static byte[] send = new byte[20];

//    private static int last;

    private static int dataNum;
    private static int toReceiveNum;

    private static int winSize;
    private static int begin = 0,end;
    private static int[] getData;


    public SRReceiveThread(int port){
        serverPort = port;

        dataNum = Easy.SR_DATA_NUM;
        toReceiveNum = dataNum;

        winSize = Easy.SR_R_WIN_SIZE;
        end = begin + winSize - 1;
        getData = new int[Easy.SEQ_NUM];

        for (int i = 0; i < getData.length; i++){
            getData[i] = 0;
        }

        try {
            serverSocket = new DatagramSocket(serverPort);
        }catch (SocketException e){

        }
    }

    @Override
    public void run(){

        while (true){

            receivePacket = new DatagramPacket(receive,receive.length);
            try{
                serverSocket.receive(receivePacket);

            }catch (IOException e){

            }

            int receiveSeq = -1;
            if(receive[1] == 'x'){
                receiveSeq = receive[0] - '0';
            }
            else {
                receiveSeq = (receive[0]-'0')*10+(receive[1]-'0');
            }

            //指定丢包概率
            if(Math.random()<0.7){

                getData[receiveSeq] = 1;

                System.out.println("-------  Server成功接收序列为"+receiveSeq+"的数据包  -------");

                if(receiveSeq >= begin && receiveSeq <= end && toReceiveNum >= 0){

                    //构造ACK报文，并发回给客户端
                    if(receiveSeq < 10){
                        send = new String("ACK"+receiveSeq+"x").getBytes();
                    }
                    else if(receiveSeq < 100){
                        send = new String("ACK"+receiveSeq).getBytes();
                    }
                    InetAddress inetAddress = receivePacket.getAddress();
                    int clientPort = receivePacket.getPort();
                    sendPacket = new DatagramPacket(send,send.length,inetAddress,clientPort);
                    try{
                        serverSocket.send(sendPacket);
                        toReceiveNum--;
                    }catch (IOException e){

                    }
                    System.out.println("-------  Server发送序列为"+receiveSeq+"的ACK  -------");

                    if(receiveSeq == begin){
                        int moveNum = 0;

                        for(int i = begin; i <= end; i++){
                            if(getData[i] == 1){
                                moveNum++;
                                getData[i] = 0;
                            }
                            else {
                                break;
                            }
                        }

                        begin += moveNum;
                        end += moveNum;
                    }
                }

                else if(receiveSeq < begin && toReceiveNum >= 0){
                    if(receiveSeq < 10){
                        send = new String("ACK"+receiveSeq+"x").getBytes();
                    }
                    else if(receiveSeq < 100){
                        send = new String("ACK"+receiveSeq).getBytes();
                    }
                    InetAddress inetAddress = receivePacket.getAddress();
                    int clientPort = receivePacket.getPort();
                    sendPacket = new DatagramPacket(send,send.length,inetAddress,clientPort);
                    try{
                        serverSocket.send(sendPacket);

                        System.out.println("-------  Server发送序列为"+receiveSeq+"的ACK  -------");

                        getData[receiveSeq] = 0;

                    }catch (IOException e){

                    }
                }
                else {
//                    System.out.println("-------  产生丢包!当前序号为："+receiveSeq+"  -------");
//                    System.out.println("-------  应当接收序号为："+(last+1)+"  -------");
//                    //返回序号为上次成功接受到的ACK
//                    if(last < 10){
//                        send = new String("ACK"+last+"x").getBytes();
//                    }
//                    else if(last < 100){
//                        send = new String("ACK"+last).getBytes();
//                    }
//                    InetAddress inetAddress = receivePacket.getAddress();
//                    int clientPort = receivePacket.getPort();
//                    sendPacket = new DatagramPacket(send,send.length,inetAddress,clientPort);
//                    try{
//                        serverSocket.send(sendPacket);
//                        toReceiveNum ++;
//                    }catch (IOException e){
//
//                    }
//                    System.out.println("-------  Server发送序列为"+last+"的ACK  -------");
                }
            }
        }
    }
}
