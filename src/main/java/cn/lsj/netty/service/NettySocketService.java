package cn.lsj.netty.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.*;

public class NettySocketService extends RequestHandler<WebSocketFrame>{
    @Override
    void requestAction(ChannelHandlerContext ctx, WebSocketFrame frame, WebSocketServerHandshaker serverShakeHand) {

        if (frame instanceof CloseWebSocketFrame) {//关闭
            serverShakeHand.close(ctx.channel(), (CloseWebSocketFrame)frame.retain());
        }else if (frame instanceof PingWebSocketFrame) {//ping消息
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
        }else if (frame instanceof TextWebSocketFrame) {//文本消息
            String request = ((TextWebSocketFrame)frame).text();
            ctx.channel().write(new TextWebSocketFrame("websocket return:"+request));
        }
    }
}
