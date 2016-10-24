package com.dusz7;

import com.dusz7.connection.HttpAgentThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Agent {

    public static void main(String[] args) {
	// write your code here

        ServerSocket serverSocket = null;
        try{
            //设置服务器进程端口号为7788
            //由于使用了含有端口号的构造器，不用再调用bind()方法
            serverSocket = new ServerSocket(7788);
            System.out.println("代理已启动！");

            //支持并发
            while (true){
                Socket socket2Client = null;
                try{
                    socket2Client = serverSocket.accept();
                    new HttpAgentThread(socket2Client).start();
                }catch (Exception ee){
                    System.out.println("线程启动失败");
                }
            }
        }catch (IOException e){
            System.out.println("代理启动失败！");
        }finally {
            try {
                serverSocket.close();
            }catch (IOException eee){
                eee.printStackTrace();
            }
        }
    }
}
