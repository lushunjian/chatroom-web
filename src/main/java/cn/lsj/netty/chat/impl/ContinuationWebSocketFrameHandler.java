package cn.lsj.netty.chat.impl;

import cn.lsj.netty.chat.WebSocketFrameHandler;
import cn.lsj.netty.constant.WebSocketConstant;
import cn.lsj.util.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
        System.out.println("continuationWebSocketFrame-----"+continuationWebSocketFrame);
        ByteBuf byteBuf=continuationWebSocketFrame.content();
        try {
            byte[] byteArray = new byte[byteBuf.capacity()];
            byteBuf.readBytes(byteArray);
            WebSocketConstant.fileOutput.write(byteArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ContinuationWebSocketFrame getContinuationWebSocketFrame() {
        return continuationWebSocketFrame;
    }

    public void setContinuationWebSocketFrame(ContinuationWebSocketFrame continuationWebSocketFrame) {
        this.continuationWebSocketFrame = continuationWebSocketFrame;
    }
}
