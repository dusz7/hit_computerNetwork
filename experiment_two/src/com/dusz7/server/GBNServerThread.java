package com.dusz7.server;

import com.dusz7.just4easy.Easy;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by dusz2 on 2016/10/25 0025.
 */
public class GBNServerThread extends Thread {
    private static int serverPort;

    private static DatagramPacket receivePacket;
    private static DatagramPacket sendPacket;
    private static DatagramSocket serverSocket;

    private static byte[] receive = new byte[1024];
    private static byte[] send = new byte[20];

    private static int last;

    private static int dataNum;
    private static int toReceiveNum;


    public GBNServerThread(){
        last = -1;
        serverPort = Easy.GBNSP;

        dataNum = Easy.DATA_NUM;
        toReceiveNum = dataNum;

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

                if (receiveSeq == last+1){
                    System.out.println("-------  Server成功接收序列为"+receiveSeq+"的数据包  -------");
                    //接收正确的序列，构造ACK报文，并发回给客户端
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
                        last++;
                    }catch (IOException e){

                    }
                    System.out.println("-------  Server发送序列为"+receiveSeq+"的ACK  -------");
                }
                else if(receiveSeq < (last+1)){
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

                    }catch (IOException e){

                    }
                }
                else if(last != -1 && toReceiveNum>0){
                    System.out.println("-------  产生丢包!当前序号为："+receiveSeq+"  -------");
                    System.out.println("-------  应当接收序号为："+(last+1)+"  -------");
                    //返回序号为上次成功接受到的ACK
                    if(last < 10){
                        send = new String("ACK"+last+"x").getBytes();
                    }
                    else if(last < 100){
                        send = new String("ACK"+last).getBytes();
                    }
                    InetAddress inetAddress = receivePacket.getAddress();
                    int clientPort = receivePacket.getPort();
                    sendPacket = new DatagramPacket(send,send.length,inetAddress,clientPort);
                    try{
                        serverSocket.send(sendPacket);
                        toReceiveNum ++;
                    }catch (IOException e){

                    }
                    System.out.println("-------  Server发送序列为"+last+"的ACK  -------");
                }
            }
        }

    }
}
