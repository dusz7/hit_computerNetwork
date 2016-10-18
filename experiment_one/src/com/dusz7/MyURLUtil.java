package com.dusz7;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by dusz2 on 2016/10/16 0016.
 */
public class MyURLUtil {

//    public static String getRequestURL(String firstLine){
//        String[] tokens = firstLine.split(" ");
//        String URL = "";
//        for (int index = 0; index < tokens.length; index++) {
//            if (tokens[index].startsWith("http://")) {
//                URL = tokens[index];
//                break;
//            }
//        }
//        return URL;
//    }

    private String scheme;
    private String host;
    private int port;
    private String resource;
    private String IP;

    public MyURLUtil(String requestUrl){
        String scheme = "http";
        String host = "";
        String port = "80"; // http应用的默认端口为80

        int index;
        index = requestUrl.indexOf("//");
        if(index != -1){
            scheme = requestUrl.substring(0,index-1);  //获取http...
            host = requestUrl.substring(index+2);  //获取host（初级）
        }

        //检查是否有资源请求
        index = host.indexOf('/');
        if(index != -1){
            this.resource = host.substring(index);  // 拿到资源
            host = host.substring(0,index);  // 获取host（去掉资源以后）
        }

        //检查是否有端口号
        index = host.indexOf(':');
        if(index != -1){
            port = host.substring(index+1);  // 获取到端口号
            host = host.substring(0,index);  // 获取到最终版host
        }

        this.scheme = scheme;
        this.host = host;
        this.port = Integer.parseInt(port);

    }

    //获取dns信息，从域名解析得到ip地址，并将dns缓存
    public String getIP() {
        java.security.Security.setProperty("networkaddress.cache.ttl", "30");
        try {
            this.IP = InetAddress.getByName(this.host).getHostAddress();
        } catch (UnknownHostException e) {
            return "";
        }
        return this.IP;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

}
