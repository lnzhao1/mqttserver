package com.unicloud.liziyun.util;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 * @author zhaoxiao 2020/3/9
 */
public class NettyUtil {

    public static final AttributeKey<String> CLIENTID_KEY = AttributeKey.valueOf("cha.clientId");

    public static String getClientId(Channel channel) {
        Attribute<String> attr = channel.attr(CLIENTID_KEY);
        return attr.get();
    }

    public static void setClientId(Channel channel, String clientId) {
        Attribute<String> attr = channel.attr(CLIENTID_KEY);
        attr.set(clientId);
    }

    public static boolean isLogin(Channel channel) {
        String clientId = (String) channel.attr(CLIENTID_KEY).get();
        return clientId != null && clientId.trim().length() > 0;
    }
}
