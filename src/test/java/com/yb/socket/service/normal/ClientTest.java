package com.yb.socket.service.normal;

import com.alibaba.fastjson.JSONObject;
import com.unicloud.liziyun.codec.JsonDecoder;
import com.unicloud.liziyun.codec.JsonEncoder;
import com.unicloud.liziyun.pojo.Request;
import com.unicloud.liziyun.pojo.Response;
import com.unicloud.liziyun.service.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author daoshenzzg@163.com
 * @date 2018/12/30 18:43
 */
public class ClientTest {
    private static final Logger logger = LoggerFactory.getLogger(ClientTest.class);

    public static void main(String[] args) throws InterruptedException {

        Client client = new Client();
        client.setIp("127.0.0.1");
        client.setPort(8000);
        client.setConnectTimeout(10000);
        client.setCheckHeartbeat(false);
        client.addChannelHandler("decoder", new JsonDecoder());
        client.addChannelHandler("encoder", new JsonEncoder());
        client.connect();

        for (int i = 0; i < 2; i++) {
            JSONObject message = new JSONObject();
            message.put("action", "echo");
            message.put("message", "hello world!");

            Request request = new Request();
            request.setSequence(i);
            request.setMessage(message);
            Response response = (Response) client.sendWithSync(request, 3000);

            logger.info("成功接收到同步的返回: '{}'.", response);
        }

        //client.shutdown();
    }
}
