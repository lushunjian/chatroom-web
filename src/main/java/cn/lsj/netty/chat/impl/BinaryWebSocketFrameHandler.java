package cn.lsj.netty.chat.impl;

import cn.lsj.netty.chat.WebSocketFrameHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @Auther: Lushunjian
 * @Date: 2018/9/1 10:12
 *    webSocket 二进制流处理类
 *    通过@Component和@Scope注解，把对象交给spring容器管理。@Scope注解注入方式为：多实例注入
 *
 */
@Scope("prototype")
@Component("binary")
public class BinaryWebSocketFrameHandler extends WebSocketFrameHandler{

    private BinaryWebSocketFrame binaryWebSocketFrame;

    public BinaryWebSocketFrameHandler(){}

    public BinaryWebSocketFrameHandler(BinaryWebSocketFrame binaryWebSocketFrame){
        this.binaryWebSocketFrame=binaryWebSocketFrame;
    }
    @Override
    public void webSocketHandler(ChannelHandlerContext ctx) {
        System.out.println("二进制消息:"+binaryWebSocketFrame);
        ByteBuf buf = binaryWebSocketFrame.content();

        for (int i = 0; i < buf.capacity(); i++){
            byte b = buf.getByte(i);
            System.out.println("byte:"+b);
        }

    }

    public BinaryWebSocketFrame getBinaryWebSocketFrame() {
        return binaryWebSocketFrame;
    }

    public void setBinaryWebSocketFrame(BinaryWebSocketFrame binaryWebSocketFrame) {
        this.binaryWebSocketFrame = binaryWebSocketFrame;
    }
}
