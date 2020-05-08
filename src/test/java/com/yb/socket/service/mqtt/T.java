package com.yb.socket.service.mqtt;

/**
 * @author zhaoxiao 2020/3/19
 */
public class T {
    public static void main(String[] args) {
        for(int i=0;i<500;i++){
            new Thread(new TestThread(String.valueOf(i))).start();
        }
    }
}
