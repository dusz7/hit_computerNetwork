package com.dusz7.user;

/**
 * Created by dusz2 on 2016/10/24 0024.
 */

//"202.89.233.104"
public class UserShield {
    private static String[] myShieldList = {"0.0.0.0"};

    public static boolean isShielded(String ip){

        for (int i = 0; i < myShieldList.length; i++){
            if(ip.contains(myShieldList[i])){
                return true;
            }
        }
        return false;

    }
}
