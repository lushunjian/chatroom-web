package cn.lsj.netty.chat.impl;

import cn.lsj.netty.chat.WebSocketFrameHandler;
import cn.lsj.util.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
        String str = ByteBufUtil.byteToString(byteBuf);
    }

    public ContinuationWebSocketFrame getContinuationWebSocketFrame() {
        return continuationWebSocketFrame;
    }

    public void setContinuationWebSocketFrame(ContinuationWebSocketFrame continuationWebSocketFrame) {
        this.continuationWebSocketFrame = continuationWebSocketFrame;
    }
}
