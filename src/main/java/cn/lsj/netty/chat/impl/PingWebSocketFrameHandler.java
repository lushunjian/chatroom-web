package cn.lsj.netty.chat.impl;

import cn.lsj.netty.chat.WebSocketFrameHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;

/**
 * @Auther: Lushunjian
 * @Date: 2018/9/1 10:06
 * @Description:  webSocket 处理心跳
 */
public class PingWebSocketFrameHandler extends WebSocketFrameHandler<PingWebSocketFrame> {
    @Override
    public void webSocketHandler(ChannelHandlerContext ctx, PingWebSocketFrame pingWebSocketFrame) {
        ctx.channel().write(new PongWebSocketFrame(pingWebSocketFrame.content().retain()));
        return;
    }
}
