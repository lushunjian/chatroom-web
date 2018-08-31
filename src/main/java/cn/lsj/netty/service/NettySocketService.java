package cn.lsj.netty.service;

import cn.lsj.netty.constant.WebSocketConstant;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.*;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NettySocketService extends RequestHandler<WebSocketFrame>{

    private static final Logger logger = Logger
            .getLogger(WebSocketServerHandshaker.class.getName());
    @Override
    void requestAction(ChannelHandlerContext ctx, WebSocketFrame frame) {

        // 判断是否关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            super.getServerHandshake().close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
        }

        // 判断是否ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        // 本例程仅支持文本消息，不支持二进制消息
        if (!(frame instanceof TextWebSocketFrame)) {
            logger.info("本例程仅支持文本消息，不支持二进制消息");
            throw new UnsupportedOperationException(String.format(
                    "%s frame types not supported", frame.getClass().getName()));
        }

        // 返回应答消息
        String request = ((TextWebSocketFrame) frame).text();
        System.out.println("服务端收到：" + request);

        if (logger.isLoggable(Level.FINE)) {
            logger.fine(String.format("%s received %s", ctx.channel(),
                            request));
        }

        TextWebSocketFrame tws = new TextWebSocketFrame(new Date().toString()
                + ctx.channel().id() + "：" + request);

        // 群发
        WebSocketConstant.group.writeAndFlush(tws);

    }
}
