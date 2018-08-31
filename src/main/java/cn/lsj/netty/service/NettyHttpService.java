package cn.lsj.netty.service;

import cn.lsj.netty.constant.WebSocketConstant;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;

public class NettyHttpService extends RequestHandler<FullHttpRequest> {

    private final static String wsUri = "/ws";


    @Override
    void requestAction(ChannelHandlerContext ctx, FullHttpRequest req) {
        System.out.println("消息进入http处理类");
        WebSocketServerHandshaker handshake=handleUpgradeRequest(ctx,req);
        //握手对象将用于web-socket连接
        super.setServerHandshake(handshake);
    }

    /**
     * 协议升级方法，如果成功返回handshake对象
     * */
    public WebSocketServerHandshaker handleUpgradeRequest(ChannelHandlerContext ctx , FullHttpRequest request){
        HttpHeaders httpHeaders = request.headers();
        //判断请求头
        /**
         * 如果HTTP解码失败，或者当前请求不是 请求升级为webSocket的http请求
         * */
        if (!request.decoderResult().isSuccess() || (!WebSocketConstant.WEBSOCKET.equals(request.headers().get(WebSocketConstant.UPGRADE).toString()))) {
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
            // 返回应答给客户端
            if (response.status().code() != 200) {
                ByteBuf buf = Unpooled.copiedBuffer(response.status().toString(),
                        CharsetUtil.UTF_8);
                response.content().writeBytes(buf);
                buf.release();
                ///HttpUtil.(response, response.content().readableBytes());
            }
            // 如果是非Keep-Alive，关闭连接
            ChannelFuture channelFuture = ctx.channel().writeAndFlush(response);
            if (!isKeepAlive(request) || response.status().code() != 200) {
                channelFuture.addListener(ChannelFutureListener.CLOSE);
            }
            return null;
        }else {
            String subProtocols=null;
            CharSequence charSequence = request.headers().get(HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL);
            if(charSequence!=null)
                subProtocols = charSequence.toString();
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                    "ws://127.0.0.1:8889/webSocket", subProtocols, false);
            WebSocketServerHandshaker handshake = wsFactory.newHandshaker(request);
            if (handshake == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
                return null;
            } else {
                handshake.handshake(ctx.channel(), request);
                //将handshaker绑定给channel
                AttributeKey<WebSocketServerHandshaker> attributeKey = WebSocketConstant.ATTR_HANDSHAKE;
                ctx.channel().attr(attributeKey).set(handshake);
                return handshake;
            }
        }
    }

    private static boolean isKeepAlive(FullHttpRequest req) {
        return false;
    }
}
