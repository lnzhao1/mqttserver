package com.unicloud.liziyun.pojo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author zhaoxiao 2020/3/10
 */
public class T {
    public static void main(String[] args) {
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        try {
//            Date date = formatter.parse("2020-03-10T13:57:00.000+00:00");
//            Date date1 = new Date();
//            System.out.println(date);
            TimeZone t = TimeZone.getDefault();
            System.out.println(t.getDisplayName());
            System.out.println("时差"+t.getOffset(System.currentTimeMillis())+"");
            String dateString = "2020-03-11T11:12:10.000+07:00";
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                Date date0 = formatter.parse(dateString);
                System.out.println("not set timezone"+ date0.toString());
                formatter.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
                Date date = formatter.parse(dateString);
                System.out.println("set timezone asia"+date.toString());
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date1 = formatter.parse(dateString);
                System.out.println("set timezone utc"+date1.toString());
                System.out.println("new "+new Date().toString());
            }catch (Exception e ){

            }
        }catch (Exception e){

        }
    }
}
