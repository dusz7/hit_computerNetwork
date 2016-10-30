package com.dusz7.cache;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dusz2 on 2016/10/25 0025.
 */
public class CacheUtil {

    private static List<String> cacheInfo;

    public static boolean isNew(Socket s2c, Socket c2s, String time){

        return true;
    }

    public static void getNew(Socket s2c, Socket c2s){

    }

    public static String findCache(String url){
        String modifyTime = null;

        cacheInfo = new ArrayList<String>();
        String resul = null;
        int count = 0;
        try {
            // 直接在存有url和相应信息的文件中查找
            InputStream file_D = new FileInputStream("log_d.txt");

            String info = "";
            while (true) {
                int c = file_D.read();
                if (c == -1)
                    break; // -1为结尾标志
                if (c == '\r') {
                    file_D.read();
                    break;// 读入每一行数据
                }
                if (c == '\n')
                    break;
                info = info + (char) c;
            }
            System.out.println("第一次得到：" + info);
            System.out.println("要找的是：" + url);
            int m = 0;
            while ((m = file_D.read()) != -1 && info!=null) {

                // 找到相同的，那么它下面的就是响应信息，找上次修改的时间
                if (info.contains(url)) {
                    String info1;
                    do {
                        System.out.println("找到相同的了：" + info);
                        info1 = "";
                        if(m!='\r' && m != '\n')
                            info1 += (char) m;
                        while (true) {
                            m = file_D.read();
                            if (m == -1)
                                break;
                            if (m == '\r') {
                                file_D.read();
                                break;
                            }
                            if (m == '\n') {
                                break;
                            }
                            info1 += (char) m;
                        }
                        System.out.println("info1是："+info1);
                        if (info1.contains("Last-Modified:")) {
                            resul = info1.substring(16);
                        }
                        cacheInfo.add(info1);
                        if(info1.equals("")){
                            System.out.print("我是空");
                            return resul;
                        }
                    } while (!info1.equals("") && info1 != null && m != -1);
                }
                info = "";
                while (true) {
                    if (m == -1)
                        break;
                    if (m == '\r') {
                        file_D.read();
                        break;
                    }
                    if (m == '\n')
                        break;
                    info += (char) m;
                    m = file_D.read();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        modifyTime = resul;

        return modifyTime;
    }
}
