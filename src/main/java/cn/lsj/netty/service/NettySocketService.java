package cn.lsj.netty.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.*;
import java.util.logging.Logger;

import static cn.lsj.netty.chat.factory.WebSocketFrameFactory.createSocketHandler;
import static cn.lsj.netty.chat.factory.WebSocketFrameFactory.createSocketHandlerBySpring;

public class NettySocketService extends RequestHandler<WebSocketFrame>{

    private static final Logger logger = Logger
            .getLogger(WebSocketServerHandshaker.class.getName());
    @Override
    void requestAction(ChannelHandlerContext ctx, WebSocketFrame frame) {

        //传统方式生成业务处理对象
        //createSocketHandler(frame).socketHand(ctx);

        //通过spring，生成业务处理对象
        createSocketHandlerBySpring(frame).socketHand(ctx);

    }
}
