package com.dusz7.connection;

import com.dusz7.url.URLFish;
import com.dusz7.url.URLShield;
import com.dusz7.url.URLUtil;
import com.dusz7.user.UserShield;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by dusz2 on 2016/10/16 0016.
 */
public class HttpAgentThread extends Thread {

    static public int CONNECT_RETRIES = 5; // 尝试与目标主机连接次数
    static public int CONNECT_PAUSE = 5; // 每次建立连接的间隔时间
    static public int TIMEOUT = 500; // 每次尝试连接的最大时间

    Socket socket2Client;  //与客户端的socket

    public HttpAgentThread(Socket socket){
        this.socket2Client = socket;
    }

    @Override
    public void run(){

        String firstLine = "";  //客户端请求报文的第一行
        String urlStr = "";  //请求中的url

        Socket socket2Server = null;  //与目标服务器的socket

        InputStream clientIS = null ,serverIS = null;
        OutputStream clientOS = null ,serverOS = null;

        try{
            socket2Client.setSoTimeout(TIMEOUT); // 为客户端连接设置timeout
            clientIS = socket2Client.getInputStream();
            clientOS = socket2Client.getOutputStream();

            //获取客户端报文第一行
            while (true){
                int c = clientIS.read();
                if(c == -1) break;  //结尾为-1
                if(c == '\r' || c == '\n') break;

                firstLine += (char) c;
            }

            //从报文中获取到访问url   -- http://balabala...
            String[] tokens = firstLine.split(" ");
            for (int index = 0; index < tokens.length; index++) {
                if (tokens[index].startsWith("http://")) {
                    urlStr = tokens[index];
                    break;
                }
            }

            if(urlStr != ""){
                // 处理url内容
                URLUtil myURL = new URLUtil(urlStr);

                //判断是否过滤该ip
                if(!UserShield.isShielded(myURL.getIP())){

                    System.out.println("-------  访问者ip地址：  " + myURL.getIP() + "  -------");

                    //判断是否过滤该url
                    if (!URLShield.isShielded(urlStr)){

                        //判断是否重定向
                        if(!URLFish.isFished(urlStr)){

                            System.out.println("-------  此次客户端访问URL为：   " + urlStr + "  -------");
                            //尝试建立与目的服务器的连接
                            int retry = CONNECT_RETRIES;
                            while (retry-- != 0) {
                                try {
                                    //建立与目的服务器连接的socket
                                    //如果采用有这两个参数的socket构造器，则不用再调用connect()方法
                                    socket2Server = new Socket(myURL.getIP(),myURL.getPort());
                                    break;

                                } catch (Exception e) {

                                }
                                // 每次尝试间隔
                                Thread.sleep(CONNECT_PAUSE);
                            }

                            //如果建立连接成功
                            if (socket2Server != null) {
                                socket2Server.setSoTimeout(TIMEOUT);
                                serverIS = socket2Server.getInputStream();
                                serverOS = socket2Server.getOutputStream();

                                serverOS.write(firstLine.getBytes());  // 转发请求头

                                createTube(clientIS , clientOS , serverIS , serverOS);  // 建立源客户端与目的服务器的通信管道
                            }

                        }
                        else {
                            System.out.println("-------  此URL被重定向：  " + urlStr + "  -------");
                            clientOS.write(URLFish.getHead().getBytes());
                            clientOS.write(URLFish.getFirst().getBytes());
                            clientOS.flush();
                        }

                    }
                    else {
                        System.out.println("-------  此URL被阻止访问：  " + urlStr + "  -------");
                    }

                }
                else {
                    System.out.println("-------  此ip地址禁止访问：  " + myURL.getIP() + "  -------");
                }

            }

            //end of connect to client
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                socket2Client.close();
                clientIS.close();
                clientOS.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            try{
                if (socket2Server != null){
                    socket2Server.close();
                    serverIS.close();
                    serverOS.close();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }  // end of run

    // 建立源客户端与目的服务器的通信通道
    public void createTube(InputStream clientIS, OutputStream clientOS, InputStream serverIS, OutputStream serverOS){

        //分别开启两个线程
        Client2ServerThread c2s = new Client2ServerThread(clientIS, serverOS);
        Server2ClientThread s2c = new Server2ClientThread(serverIS, clientOS);
        c2s.start();
        s2c.start();
        try {
            c2s.join();
            s2c.join();
        } catch (InterruptedException e1) {

        }

    }
}
