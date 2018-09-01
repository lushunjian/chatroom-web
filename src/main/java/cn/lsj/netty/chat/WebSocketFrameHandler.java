package cn.lsj.netty.chat;

import io.netty.channel.ChannelHandlerContext;

/**
 * @Auther: Lushunjian
 * @Date: 2018/9/1 09:41
 * @Description:
 */
public abstract class WebSocketFrameHandler {

    public abstract void webSocketHandler(ChannelHandlerContext ctx);

    public void socketHand(ChannelHandlerContext ctx){
        webSocketHandler(ctx);
    }
}
