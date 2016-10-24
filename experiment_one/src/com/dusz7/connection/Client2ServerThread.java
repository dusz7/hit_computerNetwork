package com.dusz7.connection;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by dusz2 on 2016/10/17 0017.
 */
public class Client2ServerThread extends Thread {
    private InputStream clientIS;
    private OutputStream serverOS;

    public Client2ServerThread(InputStream cis, OutputStream sos){
        this.clientIS = cis;
        this.serverOS = sos;
    }

    public void run() {
        int length;
        byte bytes[] = new byte[1024];
        while(true){
            try {
                if ((length = clientIS.read(bytes)) > 0) {
                    serverOS.write(bytes, 0, length);//将http请求报文写到目标主机
                    serverOS.flush();
                } else if (length < 0)
                break;
            } catch (Exception e) {

            }
        }
    }
}
