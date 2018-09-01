package cn.lsj.netty.chat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * @Auther: Lushunjian
 * @Date: 2018/9/1 09:41
 * @Description:
 */
public abstract class WebSocketFrameHandler<T extends WebSocketFrame> {

    public abstract void webSocketHandler(ChannelHandlerContext ctx, T t);

    public void socketHand(ChannelHandlerContext ctx, T t){
        webSocketHandler(ctx,t);
    }
}
