package com.dusz7.url;

/**
 * Created by dusz2 on 2016/10/24 0024.
 */

/**
 * http://cn.bing.com/
 * http://jwc.hit.edu.cn/
 * http://pku.edu.cn/
 * http://www.163.com/
 * http://www.sina.com.cn/
 * http://www.tsinghua.edu.cn/
 *
 */

public class URLShield {

    private static String[] myShieldList = {"163.com","sina.com.cn"};

    public static boolean isShielded(String url){

        for (int i = 0; i < myShieldList.length; i++){
            if(url.contains(myShieldList[i])){
                return true;
            }
        }
        return false;

    }


}
