package cn.lsj.netty.chat.impl;

import cn.lsj.netty.chat.WebSocketFrameHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component("pong")
public class PongWebSocketFrameHandler extends WebSocketFrameHandler {

    private PongWebSocketFrame pongWebSocketFrame;

    public PongWebSocketFrameHandler(){}

    public PongWebSocketFrameHandler(PongWebSocketFrame pongWebSocketFrame){
        this.pongWebSocketFrame=pongWebSocketFrame;
    }
    @Override
    public void webSocketHandler(ChannelHandlerContext ctx) {
        ctx.channel().write(new PongWebSocketFrame(pongWebSocketFrame.content().retain()));
    }

    public PongWebSocketFrame getPongWebSocketFrame() {
        return pongWebSocketFrame;
    }

    public void setPongWebSocketFrame(PongWebSocketFrame pongWebSocketFrame) {
        this.pongWebSocketFrame = pongWebSocketFrame;
    }
}
