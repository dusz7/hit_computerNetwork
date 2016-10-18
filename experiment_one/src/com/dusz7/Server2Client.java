package com.dusz7;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by dusz2 on 2016/10/17 0017.
 */
public class Server2Client extends Thread {
    private InputStream serverIS;
    private OutputStream clientOS;

    public Server2Client(InputStream sis, OutputStream cos){
        this.serverIS = sis;
        this.clientOS = cos;
    }

    public void run() {
        int length;
        byte bytes[] = new byte[1024];
        while(true){
            try {
                if ((length = serverIS.read(bytes)) > 0) {
                    clientOS.write(bytes, 0, length);//将http请求头写到目标主机
                    clientOS.flush();
                } else if (length < 0)
                    break;
            } catch (Exception e) {
                //System.out.println("\nRequest Exception:");
                //e.printStackTrace();
            }
        }
    }
}
