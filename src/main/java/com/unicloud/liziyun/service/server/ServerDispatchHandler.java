package com.unicloud.liziyun.service.server;

import com.unicloud.liziyun.listener.DefaultMqttMessageEventListener;
import com.unicloud.liziyun.service.EventDispatcher;
import com.unicloud.liziyun.service.WrappedChannel;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author daoshenzzg@163.com
 * @date 2018/12/30 16:47
 */
public class ServerDispatchHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ServerDispatchHandler.class);

    protected EventDispatcher eventDispatcher;

    public ServerDispatchHandler(EventDispatcher eventDispatcher) {
        if (eventDispatcher == null) {
            throw new IllegalArgumentException("eventDispatcher cannot be null.");
        }

        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String channelId = ctx.channel().id().asShortText();
        if (logger.isDebugEnabled()) {
            logger.debug("Message received from channel '{} : '{}'.", channelId, msg);
        }
        WrappedChannel channel = ((Server) eventDispatcher.getService()).getChannel(channelId);
        MqttMessage mqttMessage = (MqttMessage)msg;
        if(mqttMessage instanceof MqttPublishMessage){
            MqttPublishMessage mqttPublishMessage = (MqttPublishMessage)mqttMessage;
            ByteBuf echoMsg = mqttPublishMessage.content();
            echoMsg.retain();
        }
        eventDispatcher.dispatchMessageEvent(ctx, channel, msg);
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        WrappedChannel channel = new WrappedChannel(ctx.channel());
        String channelId = channel.id().asShortText();
        if (logger.isDebugEnabled()) {
            logger.debug("Connected on channel '{}'.", channelId);
        }
        ((Server) eventDispatcher.getService()).getChannels().put(channelId, channel);
        eventDispatcher.dispatchChannelEvent(ctx, channel);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        closeChannel(ctx);
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String channelId = ctx.channel().id().asShortText();
        WrappedChannel channel = ((Server) eventDispatcher.getService()).getChannel(channelId);
        if (channel != null) {
            eventDispatcher.dispatchExceptionCaught(ctx, channel, cause);
        }

        // 处理IOException，主动关闭channel
        if (cause instanceof IOException) {
            ctx.close();
            closeChannel(ctx);
        }
    }

    private void closeChannel(ChannelHandlerContext ctx) {
        String channelId = ctx.channel().id().asShortText();
        WrappedChannel channel = ((Server) eventDispatcher.getService()).getChannels().remove(channelId);
//        DefaultMqttMessageEventListener.deviceChanel.remove(channelId);
        if (channel != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Channel '{}' was closed.", channelId);
            }

            eventDispatcher.dispatchChannelEvent(ctx, channel);
        }
        ctx.close();
    }

}