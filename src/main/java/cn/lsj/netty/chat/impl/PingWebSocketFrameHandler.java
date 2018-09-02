package cn.lsj.netty.chat.impl;

import cn.lsj.netty.chat.WebSocketFrameHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @Auther: Lushunjian
 * @Date: 2018/9/1 10:06
 *  webSocket 处理心跳
 *  通过@Component和@Scope注解，把对象交给spring容器管理。@Scope注解注入方式为：多实例注入
 */
@Scope
@Component("ping")
public class PingWebSocketFrameHandler extends WebSocketFrameHandler {

    private PingWebSocketFrame pingWebSocketFrame;

    public PingWebSocketFrameHandler(){}

    public PingWebSocketFrameHandler(PingWebSocketFrame pingWebSocketFrame){
        this.pingWebSocketFrame=pingWebSocketFrame;
    }
    @Override
    public void webSocketHandler(ChannelHandlerContext ctx) {
        ctx.channel().write(new PongWebSocketFrame(pingWebSocketFrame.content().retain()));
    }

    public PingWebSocketFrame getPingWebSocketFrame() {
        return pingWebSocketFrame;
    }

    public void setPingWebSocketFrame(PingWebSocketFrame pingWebSocketFrame) {
        this.pingWebSocketFrame = pingWebSocketFrame;
    }
}
