package cn.lsj.netty.service;

import cn.lsj.domain.Message;
import cn.lsj.netty.config.NettyConfig;
import cn.lsj.netty.constant.WebSocketConstant;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.util.CharsetUtil;

import java.util.List;
import java.util.Map;

import static cn.lsj.netty.constant.WebSocketConstant.concurrentMap;
import static io.netty.handler.codec.http.websocketx.WebSocketVersion.*;

public class NettyHttpService extends RequestHandler<FullHttpRequest> {

    private NettyConfig nettyConfig;

    public NettyHttpService(NettyConfig nettyConfig){
        this.nettyConfig=nettyConfig;
    }

    @Override
    void requestAction(ChannelHandlerContext ctx, FullHttpRequest req) {
        //握手对象将用于web-socket连接，web-socket在关闭链路时需要用握手对象，挥手
        WebSocketConstant.serverHandshake= handleUpgradeRequest(ctx,req);
    }

    /**
     * 协议升级处理，如果成功返回handshake对象
     * websocket在连接时，首先发送的是http请求，此时会进入FullHttpRequest处理类，http请求头中会带 Upgrade 属性。
     * 此属性值为websocket，则说明客户端请求，将http请求升级为websocket请求。然后进入握手流程。一旦握手成功。
     * 就建立了端到端的websocket连接。成功建立连接后，后续的websocket请求会进入WebSocketFrame处理类，进行处理。
     * */
    public WebSocketServerHandshaker handleUpgradeRequest(ChannelHandlerContext ctx , FullHttpRequest request){
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
            // url:ws://127.0.0.1:8889/webSocket
            String webSocketURL =String.format(WebSocketConstant.WEB_SOCKET_URL, nettyConfig.getHost(),nettyConfig.getPort(),nettyConfig.getRoute());
            //WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(webSocketURL, subProtocols, false);
            //数据帧最大长度，合理设置可避免大数据包攻击你的服务器,默认值为：65536。这里改大点方便大文件上传
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(webSocketURL,subProtocols, false, 1024*1024*5);
            WebSocketServerHandshaker handshake = wsFactory.newHandshaker(request);
            // 握手失败
            if (handshake == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
                return null;
            }
            // 握手成功
            else {
                Channel channel = ctx.channel();
                handshake.handshake(channel, request);
                // 处理get请求，获取url上的参数信息
                QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
                Map<String, List<String>> paramMap = decoder.parameters();
                // 获取用户账号
                List<String> paramList = paramMap.get("userAccount");
                if(paramList != null && paramList.size()>0){
                    // 得到用户账号
                    String userAccount = paramList.get(0);
                    Channel userChannel = concurrentMap.get(userAccount);
                    // 如果已经存在客户端管道。说明用户已经在别的客户端登录过，给前端发消息，告诉用户账号已在异地登录，需下线
                    if(userChannel != null){
                        Message message = new Message(1);
                        TextWebSocketFrame content = new TextWebSocketFrame(JSON.toJSONString(message));
                        userChannel.writeAndFlush(content);
                        //关闭连接
                        userChannel.close();
                    }
                    // 将当前的客户端管道与用户账号绑定
                    WebSocketConstant.concurrentMap.put(userAccount, channel);
                }
                System.out.println("握手成功！-----");
                return handshake;
            }
        }
    }

    private static boolean isKeepAlive(FullHttpRequest req) {
        return false;
    }
}
