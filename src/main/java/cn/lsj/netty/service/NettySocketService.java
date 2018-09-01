package cn.lsj.netty.service;

import cn.lsj.netty.chat.impl.BinaryWebSocketFrameHandler;
import cn.lsj.netty.chat.impl.CloseWebSocketFrameHandler;
import cn.lsj.netty.chat.impl.PingWebSocketFrameHandler;
import cn.lsj.netty.constant.WebSocketConstant;
import cn.lsj.netty.chat.impl.TextWebSocketFrameHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.*;

import java.util.Date;
import java.util.logging.Logger;

public class NettySocketService extends RequestHandler<WebSocketFrame>{

    private static final Logger logger = Logger
            .getLogger(WebSocketServerHandshaker.class.getName());
    @Override
    void requestAction(ChannelHandlerContext ctx, WebSocketFrame frame) {

        // 判断是否关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            new CloseWebSocketFrameHandler().socketHand(ctx, (CloseWebSocketFrame)frame);
        }
        // 判断是否ping消息
        else if (frame instanceof PingWebSocketFrame) {
            new PingWebSocketFrameHandler().socketHand(ctx, (PingWebSocketFrame)frame);
        }
        //字符串处理
        else if(frame instanceof TextWebSocketFrame){
            new TextWebSocketFrameHandler().socketHand(ctx, (TextWebSocketFrame)frame);
        }
        // 二进制文件流
        else if (frame instanceof BinaryWebSocketFrame) {
            new BinaryWebSocketFrameHandler().socketHand(ctx, (BinaryWebSocketFrame)frame);
        }

    }
}
