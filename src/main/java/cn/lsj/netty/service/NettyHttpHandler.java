package cn.lsj.netty.service;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;

import java.util.List;
import java.util.Map;

public class NettyHttpHandler extends RequestHandler<FullHttpRequest> {
    @Override
    void requestAction(ChannelHandlerContext ctx, FullHttpRequest req) {

        System.out.println("消息进入http处理类");
        // 如果HTTP解码失败，返回HHTP异常
        if (!req.decoderResult().isSuccess()) {
            sendHttpResponse(ctx,new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        //获取url后置参数
        HttpMethod method=req.method();
        String uri=req.uri();
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
        //获取参数
        Map<String, List<String>> parameters = queryStringDecoder.parameters();
        System.out.println(parameters.get("request").get(0));
        if(method==HttpMethod.GET){
            //路由转发
            if("/webSocket".equals(uri)){
                ctx.attr(AttributeKey.valueOf("type")).set("test");
            }
        }else if(method==HttpMethod.POST){
            ctx.writeAndFlush("服务端消息");
        }else {
            System.out.println("不支持的请求");
        }
        ctx.writeAndFlush("服务端消息");
        // 构造握手响应返回，本机测试
     /*   WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "ws://"+req.headers().get(HttpHeaders.Names.HOST)+uri, null, false);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }*/
    }
    private static void sendHttpResponse(ChannelHandlerContext ctx, DefaultFullHttpResponse res) {
        // 返回应答给客户端
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        // 如果是非Keep-Alive，关闭连接
        ctx.channel().writeAndFlush(res);
    }
}
