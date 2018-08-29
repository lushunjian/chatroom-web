package cn.lsj.netty.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.*;

public class NettySocketHandler extends RequestHandler<WebSocketFrame>{
    @Override
    void requestAction(ChannelHandlerContext ctx, WebSocketFrame frame) {

      System.out.println("消息进入socket处理类");
        // 判断是否是Ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(
                    new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        //文本消息，不支持二进制消息
        if (frame instanceof TextWebSocketFrame) {
            // 返回应答消息
            String request = ((TextWebSocketFrame) frame).text();
            ctx.channel().writeAndFlush(
                    new TextWebSocketFrame(request
                            + " , 欢迎使用Netty WebSocket服务，现在时刻："
                            + new java.util.Date().toString()));
        }
    }
}
