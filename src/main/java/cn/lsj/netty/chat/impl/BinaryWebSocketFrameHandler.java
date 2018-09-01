package cn.lsj.netty.chat.impl;

import cn.lsj.netty.chat.WebSocketFrameHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

/**
 * @Auther: Lushunjian
 * @Date: 2018/9/1 10:12
 * @Description:  webSocket 二进制流处理类
 */
public class BinaryWebSocketFrameHandler extends WebSocketFrameHandler<BinaryWebSocketFrame> {
    @Override
    public void webSocketHandler(ChannelHandlerContext ctx, BinaryWebSocketFrame binaryWebSocketFrame) {
        System.out.println("二进制消息");
    }
}
