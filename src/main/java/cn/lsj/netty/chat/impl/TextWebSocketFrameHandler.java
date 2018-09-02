package cn.lsj.netty.chat.impl;

import cn.lsj.netty.chat.message.Message;
import cn.lsj.netty.constant.WebSocketConstant;
import cn.lsj.netty.chat.WebSocketFrameHandler;
import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
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
@Scope("prototype")
@Component("text")
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
        // 获取客户端发来的消息，约定为json格式
        String message = textWebSocketFrame.text();
        System.out.println("服务端收到：" + message);

        Message userMessage = JSON.parseObject(message,Message.class);
        String type=userMessage.getMessageType();
        // 私聊
        if(WebSocketConstant.WHISPER.equals(type)){
            // 获得接收者的管道流
            Channel channel = WebSocketConstant.concurrentMap.get(userMessage.getReceiver());
            // 如果为 null 表示接收方没有上线，存入数据库，并设置状态为离线消息
            if(channel == null){
                logger.info("接收者不在线 !");
            }else {
                TextWebSocketFrame content = new TextWebSocketFrame(userMessage.getMessageContent());
                channel.writeAndFlush(content);
            }
        }else {     //群聊
            logger.info("查出群中所有用户，群发!");
        }


        // 群发
        //WebSocketConstant.group.writeAndFlush(test);
    }

    public TextWebSocketFrame getTextWebSocketFrame() {
        return textWebSocketFrame;
    }

    public void setTextWebSocketFrame(TextWebSocketFrame textWebSocketFrame) {
        this.textWebSocketFrame = textWebSocketFrame;
    }
}
