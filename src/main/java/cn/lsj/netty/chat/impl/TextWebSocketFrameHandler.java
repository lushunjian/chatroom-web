package cn.lsj.netty.chat.impl;

import cn.lsj.netty.constant.WebSocketConstant;
import cn.lsj.netty.chat.WebSocketFrameHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * @Auther: Lushunjian
 * @Date: 2018/9/1 09:43
 * @Description:  webSocket 文本消息处理类
 */
public class TextWebSocketFrameHandler extends WebSocketFrameHandler<TextWebSocketFrame> {

    private static final Logger logger = Logger
            .getLogger(TextWebSocketFrameHandler.class.getName());

    @Override
    public void webSocketHandler(ChannelHandlerContext ctx, TextWebSocketFrame webSocketFrame) {
        // 返回应答消息
        String request = webSocketFrame.text();
        System.out.println("服务端收到：" + request);

        TextWebSocketFrame tws = new TextWebSocketFrame(new Date().toString()
                + ctx.channel().id() + "：" + request);
        TextWebSocketFrame test = new TextWebSocketFrame("服务器响应！");
        // 群发
        WebSocketConstant.group.writeAndFlush(test);
    }
}
