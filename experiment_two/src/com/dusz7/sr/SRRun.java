package com.dusz7.sr;

import com.dusz7.sr.receive.SRReceiveThread;
import com.dusz7.sr.send.SRSendThread;
/**
 * Created by dusz2 on 2016/10/31 0031.
 */
public class SRRun {

    private static int receivePort = 8888;
    private static int sendPort = 8899;

    public static void main(String[] args){

        SRSendThread srSendThread = new SRSendThread(sendPort);
        srSendThread.start();

//        SRReceiveThread srReceiveThread = new SRReceiveThread(receivePort);
//        srReceiveThread.start();
    }
}
