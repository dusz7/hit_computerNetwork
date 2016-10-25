package com.dusz7.server;


/**
 * Created by dusz2 on 2016/10/25 0025.
 */
public class GBNServer {

    public static void main(String[] args){
        GBNServerThread gbnServerThread = new GBNServerThread();
        gbnServerThread.run();
    }
}
