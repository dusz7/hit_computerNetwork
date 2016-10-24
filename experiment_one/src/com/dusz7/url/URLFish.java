package com.dusz7.url;

/**
 * Created by dusz2 on 2016/10/24 0024.
 */
public class URLFish {

    private static String[] myFishList = {"pku.edu.cn","tsinghua.edu.cn"};

    private static String toUrl = "http://jwc.hit.edu.cn/";

    public static String getHead(){
        String head = "";
        head = "HTTP/1.1 302 Moved Temporarily\r\n";
        return head;
    }

    public static String getFirst(){
        String first = "";
        first = "Location: "+toUrl+"\r\n\r\n";
        return first;
    }

    public static boolean isFished(String url){
        for(int i = 0; i < myFishList.length; i++){
            if(url.contains(myFishList[i])){
                return true;
            }
        }
        return false;
    }

}
