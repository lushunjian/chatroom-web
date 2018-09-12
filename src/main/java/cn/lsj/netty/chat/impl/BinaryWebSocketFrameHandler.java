package cn.lsj.netty.chat.impl;

import cn.lsj.netty.chat.chatutil.ChatFileOutput;
import cn.lsj.vo.FileBlock;
import cn.lsj.vo.FileMessage;
import cn.lsj.netty.chat.WebSocketFrameHandler;
import cn.lsj.netty.constant.WebSocketConstant;
import cn.lsj.util.ByteBufUtil;
import cn.lsj.util.FileMessageParse;
import cn.lsj.util.LinkQueue;
import cn.lsj.vo.FileQueueBean;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

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

    // 分块序号
    private static AtomicInteger num = new AtomicInteger(1);

    public BinaryWebSocketFrameHandler(){}

    public BinaryWebSocketFrameHandler(BinaryWebSocketFrame binaryWebSocketFrame){
        this.binaryWebSocketFrame=binaryWebSocketFrame;
    }

    @Override
    public void webSocketHandler(ChannelHandlerContext ctx) {
        ByteBuf byteBuf=binaryWebSocketFrame.content();
        // 通道id
        String channelId = ctx.channel().id().asLongText();
        FileQueueBean fileQueueBean = WebSocketConstant.fileBlockMap.get(channelId);

        if(fileQueueBean == null){
            System.out.println("离线文件！！");
        }else {
            ChatFileOutput.fileOutput(binaryWebSocketFrame,byteBuf,fileQueueBean);
        }

    }

    public BinaryWebSocketFrame getBinaryWebSocketFrame() {
        return binaryWebSocketFrame;
    }

    public void setBinaryWebSocketFrame(BinaryWebSocketFrame binaryWebSocketFrame) {
        this.binaryWebSocketFrame = binaryWebSocketFrame;
    }

    /**
     *      Content-Type:multipart/file
     *      Accept-Encoding:utf-8
     *      File-Length:
     *      File-Block-Size:
     *      File-Name:
     *      Param-Boundary:--asvc
     *      --asvc
     *      name="senderAccount"
     *      111
     *
     *      --asvc
     *      name="receiverAccount"
     *      222
     *
     *      --asvc
     *      name="sendTime"
     *      131231313123
     *
     *      --asvc
     *
     */

}
