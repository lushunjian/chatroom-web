package cn.lsj.netty.chat.impl;

import cn.lsj.netty.chat.WebSocketFrameHandler;
import cn.lsj.netty.constant.WebSocketConstant;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;

/**
 * @Auther: Lushunjian
 * @Date: 2018/9/1 10:10
 * @Description:   webSocket 链路关闭处理类
 */
public class CloseWebSocketFrameHandler extends WebSocketFrameHandler<CloseWebSocketFrame> {
    @Override
    public void webSocketHandler(ChannelHandlerContext ctx, CloseWebSocketFrame closeWebSocketFrame) {
        WebSocketConstant.serverHandshake.close(ctx.channel(), closeWebSocketFrame.retain());
    }
}
