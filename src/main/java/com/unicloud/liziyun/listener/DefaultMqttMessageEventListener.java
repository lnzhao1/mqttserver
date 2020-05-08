package com.unicloud.liziyun.listener;

import com.unicloud.liziyun.exception.SocketRuntimeException;
import com.unicloud.liziyun.pojo.PersonnelLocus;
import com.unicloud.liziyun.pojo.ReceiveWarn;
import com.unicloud.liziyun.service.PersonnelLocusService;
import com.unicloud.liziyun.service.ReceiveWarnServcie;
import com.unicloud.liziyun.service.WrappedChannel;
import com.unicloud.liziyun.service.server.Server;
import com.unicloud.liziyun.service.server.ServerContext;
import com.unicloud.liziyun.util.MqttProtocolUtil;
import com.unicloud.liziyun.util.NettyUtil;
import com.unicloud.liziyun.util.SpringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.alibaba.fastjson.util.IOUtils.UTF8;

/**
 * @author daoshenzzg@163.com
 * @date 2019/1/4 11:17
 */
public class DefaultMqttMessageEventListener implements MessageEventListener {
    private static final Logger logger = LoggerFactory.getLogger(DefaultMqttMessageEventListener.class);

    @Override
    public EventBehavior channelRead(ChannelHandlerContext ctx, WrappedChannel channel, Object msg) {
        System.out.println(msg.toString());
        if (msg instanceof MqttMessage) {
            MqttMessage message = (MqttMessage) msg;
            MqttMessageType messageType = message.fixedHeader().messageType();
            switch (messageType) {
                case CONNECT:
                    this.connect(channel, (MqttConnectMessage) message);
                    break;
                case PUBLISH:
                    this.publish(channel, (MqttPublishMessage) message);
                    break;
                case SUBSCRIBE:
                    this.subscribe(channel, (MqttSubscribeMessage) message);
                    break;
                case UNSUBSCRIBE:
                    this.unSubscribe(channel, (MqttUnsubscribeMessage) message);
                    break;
                case PINGREQ:
                    this.pingReq(channel, message);
                    break;
                case DISCONNECT:
                    this.disConnect(channel, message);
                    break;
                default:
                    if (logger.isDebugEnabled()) {
                        logger.debug("Nonsupport server message  type of '{}'.", messageType);
                    }
                    break;
            }
        }
        return EventBehavior.CONTINUE;
    }

    public void connect(WrappedChannel channel, MqttConnectMessage msg) {
        String clientId = msg.payload().clientIdentifier();
        if (logger.isDebugEnabled()) {
            logger.debug("MQTT CONNECT received on channel '{}', clientId is '{}'.",
                    channel.id().asShortText(), clientId);
        }
        System.out.println(channel.id().asShortText());
//        deviceChanel.put(channel.id().asShortText(),clientId);
        NettyUtil.setClientId(channel,clientId);
        //todo verify password username msg.payload().userName(); disConnect()
//        if("admin".equals(msg.payload().userName()) && "123456".equals(msg.payload().password())){
            MqttConnAckMessage okResp = (MqttConnAckMessage) MqttMessageFactory.newMessage(new MqttFixedHeader(
                            MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                    new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, true), null);
            channel.writeAndFlush(okResp);
//        }else {
//            disConnect(channel, msg);
//        }
    }

    public void pingReq(WrappedChannel channel, MqttMessage msg) {
        if (logger.isDebugEnabled()) {
            logger.debug("MQTT pingReq received.");
        }

        Server server = ServerContext.getContext().getServer();
        if(server != null) {
            server.getCountInfo().getHeartbeatNum().incrementAndGet();
        }

        MqttMessage pingResp = new MqttMessage(new MqttFixedHeader(MqttMessageType.PINGRESP, false,
                MqttQoS.AT_MOST_ONCE, false, 0));
        channel.writeAndFlush(pingResp);
    }

    public void disConnect(WrappedChannel channel, MqttMessage msg) {
        if (channel.isActive()) {
            channel.close();
//            deviceChanel.remove(channel.id().asShortText());
            if (logger.isDebugEnabled()) {
                logger.debug("MQTT channel '{}' was closed.", channel.id().asShortText());
            }
        }
    }

    public void publish(WrappedChannel channel, MqttPublishMessage msg) {
        String topic = msg.variableHeader().topicName();
        ByteBuf buf = msg.content().duplicate();
        byte[] tmp = new byte[buf.readableBytes()];
        MqttQoS qosLevel = msg.fixedHeader().qosLevel();
        buf.readBytes(tmp);
        String content = new String(tmp);
        String t = NettyUtil.getClientId(channel);
        if(t != null){
            logger.warn(t.toString());
        }
        logger.warn(content);
        int packetId = msg.variableHeader().packetId();
        if(content.contains("301")){
            saveWarn(t,content);
        }else {
            saveLocation(t,content);
        }
        if (qosLevel == MqttQoS.AT_MOST_ONCE) {
        } else if (qosLevel == MqttQoS.AT_LEAST_ONCE) {
            channel.writeAndFlush(MqttProtocolUtil.pubAckMessage( packetId));
        } else if (qosLevel == MqttQoS.EXACTLY_ONCE) {
            channel.writeAndFlush(MqttProtocolUtil.pubRecMessage( packetId));
        }
//        MqttPublishMessage sendMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
//                new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.AT_MOST_ONCE, isRetain, content.length()),
//                new MqttPublishVariableHeader(topic, packetId),
//                Unpooled.buffer().writeBytes(new String(content.toUpperCase()).getBytes()));
    }

    public void subscribe(WrappedChannel channel, MqttSubscribeMessage msg) {
        MqttSubAckMessage subAckMessage = (MqttSubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(msg.variableHeader().messageId()),
                new MqttSubAckPayload(0));
        channel.writeAndFlush(subAckMessage);
    }

    public void unSubscribe(WrappedChannel channel, MqttUnsubscribeMessage msg) {
        MqttUnsubAckMessage unSubAckMessage = (MqttUnsubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(msg.variableHeader().messageId()), null);
        channel.writeAndFlush(unSubAckMessage);
    }

    private static String validatePayload(String sessionId, ByteBuf payloadData, boolean isEmptyPayloadAllowed) throws SocketRuntimeException {
        String payload = payloadData.toString(UTF8);
        if (payload == null) {
            if (!isEmptyPayloadAllowed) {
                throw new SocketRuntimeException(new IllegalArgumentException("Payload is empty!"));
            }
        }
        return payload;
    }
    /**
     * description: 保存报警
     * @Param sncode:
     * @Param content:
     * @return void:
     * @author: zhaoxiao
     * @createTime: 2020/3/11 15:28
    */
    private  void  saveWarn(String sncode,String content){
            String[] datalist = content.split(",");
            ReceiveWarn receiveLoc = new ReceiveWarn();
            receiveLoc.setSncode(sncode);
            for (int i = 0; i<datalist.length; i++){
                switch (i){
                    case 0:
                        receiveLoc.setStatus(datalist[i]);
                        break;
                    case 1:
                        receiveLoc.setType(datalist[i]);
                        break;
                    case 2:
                        receiveLoc.setWarnTypeDesc(datalist[i]);
                        break;
                    case 3:
                        receiveLoc.setKeyname(datalist[i]);
                        break;
                    case 4:
                        receiveLoc.setRealname(datalist[i]);
                        break;
                    case 5:
                        receiveLoc.setValue(datalist[i]);
                        break;
                    default:
                        break;

                }
            }
        receiveLoc.setIsFixed(0);
        receiveLoc.setCreatetime(new Date());
        ReceiveWarnServcie receiveDataService = (ReceiveWarnServcie) SpringUtil.getBean("receiveWarnServcie");
        receiveDataService.save(receiveLoc);
    }

    /**
     * description: 保存坐标
     * @Param sncode:
     * @Param content:
     * @return void:
     * @author: zhaoxiao
     * @createTime: 2020/3/11 15:30
    */
    private  void  saveLocation(String sncode,String content){
        String[] item = content.split("\n");
        PersonnelLocus personnelLocus = new PersonnelLocus();
        for(int j = 0; j<item.length; j++) {
            String[] datalist = item[j].split(",");
            personnelLocus.setDeviceId(sncode);
            switch (j) {
                case 0:
                    personnelLocus.setLat(Double.valueOf(datalist[3]));
                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                        Date date = formatter.parse(datalist[5]);
                        logger.info("date "+date.toString());
                        personnelLocus.setCreateDate(date);
                    }catch (Exception e){
                        logger.info(e.getMessage());
                    }
                    break;
                case 1:
                    personnelLocus.setLon(Double.valueOf(datalist[3]));
                    break;
                case 2:
                    personnelLocus.setSpeed(Double.valueOf(datalist[3]));
                    break;
                case 3:
                    personnelLocus.setHeading(Double.valueOf(datalist[3]));
                    break;
                case 4:
                    personnelLocus.setAltitude(Double.valueOf(datalist[3]));
                    break;
                case 5:
                    personnelLocus.setAcy(Double.valueOf(datalist[3]));
                    break;
                case 6:
                    personnelLocus.setBat(Double.valueOf(datalist[3]));
                    break;
                default:
                    break;
            }
        }
        PersonnelLocusService receiveDataService = (PersonnelLocusService) SpringUtil.getBean("personnelLocusService");
        receiveDataService.save(personnelLocus);
    }
}
