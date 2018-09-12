package cn.lsj.netty.chat.impl;

import cn.lsj.netty.chat.WebSocketFrameHandler;
import cn.lsj.netty.chat.chatutil.ChatFileOutput;
import cn.lsj.netty.constant.WebSocketConstant;
import cn.lsj.util.ByteBufUtil;
import cn.lsj.util.LinkQueue;
import cn.lsj.vo.FileBlock;
import cn.lsj.vo.FileMessage;
import cn.lsj.vo.FileQueueBean;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

/**
 * @Auther: Lushunjian
 * @Date: 2018/9/8 11:58
 * @Description:
 */
@Scope("prototype")
@Component("continuation")
public class ContinuationWebSocketFrameHandler extends WebSocketFrameHandler {

    private ContinuationWebSocketFrame continuationWebSocketFrame;

    @Override
    public void webSocketHandler(ChannelHandlerContext ctx) {
        System.out.println("continuationWebSocketFrame-----"+continuationWebSocketFrame+"此帧是否结束--"+continuationWebSocketFrame.isFinalFragment());
        ByteBuf byteBuf=continuationWebSocketFrame.content();
        FileQueueBean fileQueueBean = WebSocketConstant.fileBlockMap.get(ctx.channel().id().asLongText());
         ChatFileOutput.fileOutput(continuationWebSocketFrame,byteBuf,fileQueueBean);
    }

    public ContinuationWebSocketFrame getContinuationWebSocketFrame() {
        return continuationWebSocketFrame;
    }

    public void setContinuationWebSocketFrame(ContinuationWebSocketFrame continuationWebSocketFrame) {
        this.continuationWebSocketFrame = continuationWebSocketFrame;
    }
}
