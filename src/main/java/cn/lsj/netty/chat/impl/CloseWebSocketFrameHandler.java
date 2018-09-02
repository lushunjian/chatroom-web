package cn.lsj.netty.chat.impl;

import cn.lsj.netty.chat.WebSocketFrameHandler;
import cn.lsj.netty.constant.WebSocketConstant;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @Auther: Lushunjian
 * @Date: 2018/9/1 10:10
 *    webSocket 链路关闭处理类
 *    通过@Component和@Scope注解，把对象交给spring容器管理。@Scope注解注入方式为：多实例注入
 */
@Scope
@Component("close")
public class CloseWebSocketFrameHandler extends WebSocketFrameHandler{

    private  CloseWebSocketFrame closeWebSocketFrame;

    public CloseWebSocketFrameHandler(){}

    public CloseWebSocketFrameHandler( CloseWebSocketFrame closeWebSocketFrame){
        this.closeWebSocketFrame=closeWebSocketFrame;
    }

    @Override
    public void webSocketHandler(ChannelHandlerContext ctx) {
        WebSocketConstant.serverHandshake.close(ctx.channel(), closeWebSocketFrame.retain());
    }

    public CloseWebSocketFrame getCloseWebSocketFrame() {
        return closeWebSocketFrame;
    }

    public void setCloseWebSocketFrame(CloseWebSocketFrame closeWebSocketFrame) {
        this.closeWebSocketFrame = closeWebSocketFrame;
    }
}
