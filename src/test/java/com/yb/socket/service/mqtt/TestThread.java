package com.yb.socket.service.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zhaoxiao 2020/3/19
 */
public class TestThread implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(TestThread.class);
    private String message;
    public TestThread(String message) { this.message = message; }

    @Override
    public void run() {
        final String broker = "tcp://127.0.0.1:1883";
        final String clientId = "GID_XXX@@@ClientID_"+this.message;
        final String topic = "yb/notice/";
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            final MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            final MqttConnectOptions connOpts = new MqttConnectOptions();
            logger.info("Connecting to broker: {}", broker);
            connOpts.setServerURIs(new String[]{broker});
            connOpts.setUserName("admin");
            connOpts.setPassword("123456".toCharArray());
            connOpts.setCleanSession(true);
            connOpts.setKeepAliveInterval(90);
            connOpts.setAutomaticReconnect(true);
            connOpts.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
            sampleClient.setCallback(new MqttCallbackExtended() {
                public void connectComplete(boolean reconnect, String serverURI) {
                    logger.info("connect success");
                    //连接成功，需要上传客户端所有的订阅关系

                    try {
                        sampleClient.subscribe(topic,0);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                public void connectionLost(Throwable throwable) {
                    logger.error("server connection lost.", throwable);
                }

                public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                    logger.info("message arrived. topic={}, message={}.", topic, new String(mqttMessage.getPayload()));
                }

                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    logger.info("delivery complete. messageId={}.", iMqttDeliveryToken.getMessageId());
                    System.out.println();
                }
            });
            sampleClient.connect(connOpts);
            while (true) {
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                    String dateStr  = simpleDateFormat.format(new Date());
                    StringBuffer content = new StringBuffer("200,lat,lat,22.1,," + dateStr+"\n");
                    content.append("200,,,114.16,,").append(dateStr+"\n");
                    content.append("200,,,2.1,,").append(dateStr+"\n");
                    content.append("200,,,12.7,,").append(dateStr+"\n");
                    content.append("200,,,95.7,,").append(dateStr+"\n");
                    content.append("200,,,1.2,,").append(dateStr+"\n");
                    content.append("200,,,0.99,,").append(dateStr);
                    //此处消息体只需要传入 byte 数组即可，对于其他类型的消息，请自行完成二进制数据的转换
                    final MqttMessage message = new MqttMessage(content.toString().getBytes());
                    message.setQos(1);
                    /**
                     *消息发送到某个主题 Topic，所有订阅这个 Topic 的设备都能收到这个消息。
                     * 遵循 MQTT 的发布订阅规范，Topic 也可以是多级 Topic。此处设置了发送到二级 Topic
                     */
                    sampleClient.publish(topic, message);
                    Thread.sleep(30000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
//            sampleClient.disconnect();
        } catch (Exception me) {
            me.printStackTrace();
        }
    }
}
