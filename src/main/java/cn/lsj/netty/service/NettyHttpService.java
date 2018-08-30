package cn.lsj.netty.service;

import com.sun.javafx.binding.StringFormatter;
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

import java.net.URI;
import java.util.List;
import java.util.Map;

public class NettyHttpService extends RequestHandler<FullHttpRequest> {

    private final static String wsUri = "/ws";


    @Override
    void requestAction(ChannelHandlerContext ctx, FullHttpRequest req) {
        System.out.println("消息进入http处理类");

        if(handleUpgradeRequest(ctx,req)){
             doHandshake(ctx,req);
        }

/*        // 如果HTTP解码失败，返回HHTP异常
        if (!req.decoderResult().isSuccess() || "websocket".equals(req.headers().get("Upgrade").toString())) {
           sendHttpResponse(ctx, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));


            System.out.println("create WebSocket connection");
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws:/"+ctx.channel()+ "/webSocket", null, false);
            WebSocketServerHandshaker serverShakeHand = wsFactory.newHandshaker(req);//通过创建请求生成一个握手对象
            if(serverShakeHand != null) {
                serverShakeHand.handshake(ctx.channel(),req);
            }else {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            }
        }*/

    }

    /*
     * 握手连接
     *
     *
     * */
    public WebSocketServerHandshaker doHandshake(ChannelHandlerContext ctx , FullHttpRequest request ){
        HttpHeaders httpHeaders = request.headers();
        String protocols = httpHeaders.get("Sec-WebSocket-Protocol").toString();
//        "Sec-WebSocket-Protocol" -> "location.do, default.do"
        String host = httpHeaders.get("Host").toString();
        String uri = request.uri();
        String webAddress = StringFormatter.format("ws://%s" , host).getValueSafe() + uri;

        //设置最大帧长度，保证安全
        int frameLength = 10 * 1024 * 1024;
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                webAddress , protocols , true , frameLength );

        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(request);
        if (handshaker == null) {
            //版本不兼容
            WebSocketServerHandshakerFactory
                    .sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), request );
        }
        return handshaker;
    }

    public boolean handleUpgradeRequest(ChannelHandlerContext ctx , FullHttpRequest request){
        HttpHeaders httpHeaders = request.headers();
        //判断请求头
        if (!request.decoderResult().isSuccess()
                || (!"websocket".equals(request.headers().get("Upgrade").toString()))) {
            DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
            // 返回应答给客户端
            if (defaultFullHttpResponse.status().code() != 200) {
                ByteBuf buf = Unpooled.copiedBuffer(defaultFullHttpResponse.status().toString(),
                        CharsetUtil.UTF_8);
                defaultFullHttpResponse.content().writeBytes(buf);
                buf.release();
            }
            // 如果是非Keep-Alive，关闭连接
            ChannelFuture f = ctx.channel().writeAndFlush(defaultFullHttpResponse);

            if ( defaultFullHttpResponse.status().code() != 200) {
                f.addListener(ChannelFutureListener.CLOSE);
            }
            return false;
        }
        return true;
    }


    private static void sendHttpResponse(ChannelHandlerContext ctx, DefaultFullHttpResponse res) {
        // 返回应答给客户端
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        // 如果是非Keep-Alive，关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        f.addListener(ChannelFutureListener.CLOSE);
    }
}
