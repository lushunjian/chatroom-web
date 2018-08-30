package cn.lsj.netty.service;

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

import java.util.List;
import java.util.Map;

public class NettyHttpService extends RequestHandler<FullHttpRequest> {

    private final static String wsUri = "/ws";
    @Override
    void requestAction(ChannelHandlerContext ctx, FullHttpRequest req) {

        System.out.println("消息进入http处理类");
        // 如果HTTP解码失败，返回HHTP异常
        if (!req.decoderResult().isSuccess()) {
            sendHttpResponse(ctx, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        // 如果请求头中Upgrade属性是webSocket，则将http请求升级为webSocket
        if("websocket".equals(req.headers().get("Upgrade").toString())) {
            System.out.println("create WebSocket connection");
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws:/"+ctx.channel()+ "/webSocket", null, false);
            WebSocketServerHandshaker serverShakeHand = wsFactory.newHandshaker(req);//通过创建请求生成一个握手对象
            if(serverShakeHand != null) {
                serverShakeHand.handshake(ctx.channel(),req);
            }else {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            }
        }
        //获取url后置参数
       /* HttpMethod method = req.method();
        QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
        for(Map.Entry entry : decoder.parameters().entrySet()){
            System.out.println("后台取值："+entry.getKey()+"-----"+ entry.getValue());
        }
        String uri = req.uri();
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
        //获取参数
        Map<String, List<String>> parameters = queryStringDecoder.parameters();
        System.out.println(parameters);*/
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
