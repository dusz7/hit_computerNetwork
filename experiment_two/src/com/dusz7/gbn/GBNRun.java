package com.dusz7.gbn;

import com.dusz7.gbn.receive.GBNReceiveThread;
import com.dusz7.gbn.send.GBNSendThread;

/**
 * Created by dusz2 on 2016/10/25 0025.
 */
public class GBNRun {

    private static final int serverPort = 7777;
    private static final int clientPort = 7799;

    public static void main(String[] args){

        //模拟双边通信

        GBNReceiveThread gbnReceiveThread = new GBNReceiveThread(serverPort);
        gbnReceiveThread.start();

        GBNSendThread gbnSendThread = new GBNSendThread(clientPort);
        gbnSendThread.start();


    }
}
