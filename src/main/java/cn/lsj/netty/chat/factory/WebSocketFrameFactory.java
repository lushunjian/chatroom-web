package cn.lsj.netty.chat.factory;

import cn.lsj.netty.chat.WebSocketFrameHandler;
import cn.lsj.netty.chat.exception.ChatException;
import cn.lsj.netty.chat.impl.*;
import cn.lsj.netty.chat.spring.ApplicationContextProvider;
import cn.lsj.netty.constant.WebSocketConstant;
import io.netty.handler.codec.http.websocketx.*;

/**
 * @Auther: Lushunjian
 * @Date: 2018/9/1 15:39
 * @Description:
 */
public class WebSocketFrameFactory {

    /**
     * 传统方式写法，手动生成实例
     * */
    public static WebSocketFrameHandler createSocketHandler(WebSocketFrame frame){
        // 判断是否关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            return new CloseWebSocketFrameHandler((CloseWebSocketFrame)frame);
        }
        // 判断是否ping消息
        else if (frame instanceof PingWebSocketFrame) {
            return new PingWebSocketFrameHandler((PingWebSocketFrame)frame);
        }
        //字符串处理
        else if(frame instanceof TextWebSocketFrame){
            return new TextWebSocketFrameHandler((TextWebSocketFrame)frame);
        }
        // 二进制文件流
        else if (frame instanceof BinaryWebSocketFrame) {
            return new BinaryWebSocketFrameHandler((BinaryWebSocketFrame)frame);
        }
        else {
            throw ChatException.error("没有找到对应socket的处理类");
        }
    }

    /**
     * 通过spring 实例化bean
     * */
    public static WebSocketFrameHandler createSocketHandlerBySpring(WebSocketFrame frame){
        // 判断是否关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            CloseWebSocketFrameHandler frameHandler =  ApplicationContextProvider.getBean("close",CloseWebSocketFrameHandler.class);
            frameHandler.setCloseWebSocketFrame((CloseWebSocketFrame)frame);
            return frameHandler;
        }
        /**
         * 当电脑浏览器发送pong帧的时候，由于内容为空，于是服务器将空内容转发回去，导致客户端浏览器以为是错误的帧类型，发送关闭信息进行error关闭。
         * ie11会在双方都不收发消息的情况下每隔30秒发一个pong帧，iOS 7上的safari和Android 5.0自带浏览器则不会发送任何pong帧。
         * Chrome浏览器似乎只会发ping帧，而ie浏览器既发ping帧也发pong帧，发送个对应的帧回复，否则会导致连接关闭
         * */
        // 判断是否ping消息
        else if (frame instanceof PingWebSocketFrame) {
            PingWebSocketFrameHandler frameHandler = ApplicationContextProvider.getBean("ping",PingWebSocketFrameHandler.class);
            frameHandler.setPingWebSocketFrame((PingWebSocketFrame)frame);
            return frameHandler;
        }
        //判断是否pong消息
        else if (frame instanceof PongWebSocketFrame) {
            PongWebSocketFrameHandler frameHandler = ApplicationContextProvider.getBean("pong",PongWebSocketFrameHandler.class);
            frameHandler.setPongWebSocketFrame((PongWebSocketFrame)frame);
            return frameHandler;
        }
        //字符串处理
        else if(frame instanceof TextWebSocketFrame){
            TextWebSocketFrameHandler frameHandler = ApplicationContextProvider.getBean("text",TextWebSocketFrameHandler.class);
            frameHandler.setTextWebSocketFrame((TextWebSocketFrame)frame);
            return frameHandler;
        }
        // 二进制文件流
        else if (frame instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrameHandler frameHandler = ApplicationContextProvider.getBean("binary",BinaryWebSocketFrameHandler.class);
            frameHandler.setBinaryWebSocketFrame((BinaryWebSocketFrame)frame);
            return frameHandler;
        }
        // 包含二进制数据或文本数据，BinaryWebSocketFrame和TextWebSocketFrame的结合体。这个类型的数据是BinaryWebSocketFrame或TextWebSocketFrame数据中粘包导致
        else if(frame instanceof ContinuationWebSocketFrame){
            ContinuationWebSocketFrameHandler frameHandler = ApplicationContextProvider.getBean("continuation",ContinuationWebSocketFrameHandler.class);
            frameHandler.setContinuationWebSocketFrame((ContinuationWebSocketFrame)frame);
            return frameHandler;
        }
        else {
            System.out.println("暂不支持这种处理"+frame.getClass().getName());
            //throw ChatException.error("没有找到对应socket的处理类");
            return null;
        }
    }
}
