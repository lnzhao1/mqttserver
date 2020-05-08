package com.unicloud.liziyun.service.server;

import com.unicloud.liziyun.listener.DefaultMqttMessageEventListener;
import com.unicloud.liziyun.pojo.Heartbeat;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author daoshenzzg@163.com
 * @date 2018/12/30 16:13
 */
@ChannelHandler.Sharable
public class ServerHeartbeatHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ServerHeartbeatHandler.class);

    public ServerHeartbeatHandler() {
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.WRITER_IDLE) {
                System.out.println("WRITER IDLE");
            } else if (e.state() == IdleState.READER_IDLE) {
                System.out.println("read IDLE");
                ctx.channel().close();
            } else if (e.state() == IdleState.ALL_IDLE) {
                System.out.println("ALL_IDLE");
//                DefaultMqttMessageEventListener.deviceChanel.remove(ctx.channel().id().asShortText());
                ctx.close();
                return ;
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Heartbeat) {
            if (logger.isDebugEnabled()) {
                logger.debug("Heartbeat received.");
            }

            Server server = ServerContext.getContext().getServer();
            if(server != null) {
                server.getCountInfo().getHeartbeatNum().incrementAndGet();
            }
            return;
        }
        super.channelRead(ctx, msg);
    }
}
