package cn.lsj.netty.chat.impl;

import cn.lsj.netty.constant.WebSocketConstant;
import cn.lsj.netty.chat.WebSocketFrameHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Auther: Lushunjian
 * @Date: 2018/9/1 09:43
 *   webSocket 文本消息处理类
 *   通过@Component和@Scope注解，把对象交给spring容器管理。@Scope注解注入方式为：多实例注入
 */
@Component("text")
@Scope
public class TextWebSocketFrameHandler extends WebSocketFrameHandler {

    private static final Logger logger = Logger
            .getLogger(TextWebSocketFrameHandler.class.getName());

    private TextWebSocketFrame textWebSocketFrame;

    public TextWebSocketFrameHandler(){}

    public TextWebSocketFrameHandler(TextWebSocketFrame webSocketFrame){
        this.textWebSocketFrame=webSocketFrame;
    }

    @Override
    public void webSocketHandler(ChannelHandlerContext ctx) {
        // 返回应答消息
        String request = textWebSocketFrame.text();
        System.out.println("服务端收到：" + request);

        TextWebSocketFrame tws = new TextWebSocketFrame(new Date().toString()
                + ctx.channel().id() + "：" + request);
        TextWebSocketFrame test = new TextWebSocketFrame("服务器响应！");
        // 群发
        WebSocketConstant.group.writeAndFlush(test);
    }

    public TextWebSocketFrame getTextWebSocketFrame() {
        return textWebSocketFrame;
    }

    public void setTextWebSocketFrame(TextWebSocketFrame textWebSocketFrame) {
        this.textWebSocketFrame = textWebSocketFrame;
    }
}
