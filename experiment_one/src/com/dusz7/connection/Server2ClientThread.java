package com.dusz7.connection;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by dusz2 on 2016/10/17 0017.
 */
public class Server2ClientThread extends Thread {
    private InputStream serverIS;
    private OutputStream clientOS;

    public Server2ClientThread(InputStream sis, OutputStream cos){
        this.serverIS = sis;
        this.clientOS = cos;
    }

    public void run() {
        int length;
        byte bytes[] = new byte[1024];
        while(true){
            try {
                String test = "";
                if ((length = serverIS.read(bytes)) > 0) {

//                    test+=serverIS.read(bytes);

                    clientOS.write(bytes, 0, length);
                    clientOS.flush();
                } else if (length < 0)
//                    System.out.print(test);
                    break;
            } catch (Exception e) {

            }
        }
    }
}
