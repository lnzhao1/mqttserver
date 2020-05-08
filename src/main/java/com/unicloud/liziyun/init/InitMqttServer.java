package com.unicloud.liziyun.init;


import com.unicloud.liziyun.listener.DefaultMqttMessageEventListener;
import com.unicloud.liziyun.service.SocketType;
import com.unicloud.liziyun.service.server.Server;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author zhaoxiao 2020/2/12
 */
@Component
public class InitMqttServer {

    @PostConstruct
    public void initServer() {
        Server server = new Server();
        server.setPort(1883);
        server.setOpenCount(true);
        server.setCheckHeartbeat(true);
        server.setOpenStatus(true);
        server.addEventListener(new DefaultMqttMessageEventListener());
        server.setSocketType(SocketType.MQTT);
        server.bind();
    }
}
