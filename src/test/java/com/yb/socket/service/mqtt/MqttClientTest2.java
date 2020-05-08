package com.yb.socket.service.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

;

/**
 * @author daoshenzzg@163.com
 * @date 2018/12/30 18:40
 */
public class MqttClientTest2 {
    private static final Logger logger = LoggerFactory.getLogger(MqttServerTest.class);

    public static void main(String[] args) {
        final String broker = "tcp://127.0.0.1:8000";
        final String clientId = "GID_XXX@@@ClientID_124";//可设置为sn号
        final String topic = "yb/notice/";
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            final MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            final MqttConnectOptions connOpts = new MqttConnectOptions();
            logger.info("Connecting to broker: {}", broker);
            connOpts.setServerURIs(new String[]{broker});
            connOpts.setCleanSession(true);
            connOpts.setKeepAliveInterval(90);
            connOpts.setAutomaticReconnect(true);
            connOpts.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
            char[] password = new char[]{'1','2','3','4','5','6'};
            connOpts.setPassword(password);
            connOpts.setUserName("admin");
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
                }
            });
            sampleClient.connect(connOpts);
//            byte[] payload = new byte[]{'0','0','1','1'};
//            for (int i=0;i<5;i++){
//                sampleClient.publish("/yb/notice2",payload,1,true);
//            }
//            for (int i=0;i<5;i++){
//                sampleClient.publish("/yb/notice2",new MqttMessage(payload));
//
//            }
        } catch (Exception me) {
            me.printStackTrace();
        }
    }
}
