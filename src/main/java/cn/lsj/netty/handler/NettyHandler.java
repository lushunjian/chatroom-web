package cn.lsj.netty.handler;

import cn.lsj.netty.config.NettyConfig;
import cn.lsj.netty.constant.WebSocketConstant;
import cn.lsj.netty.service.NettyHttpService;
import cn.lsj.netty.service.NettySocketService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: Lushunjian
 * @Date: 2018/8/21 22:55
 * @Description:
 */
public class NettyHandler extends SimpleChannelInboundHandler<Object> {

    private NettyConfig nettyConfig;

    public NettyHandler(NettyConfig nettyConfig){
        this.nettyConfig=nettyConfig;
    }

    public static Map<String,ChannelHandlerContext> channelMap = new HashMap<String,ChannelHandlerContext>();

    /*
     * 建立连接时，返回消息
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接的客户端地址:" + ctx.channel().remoteAddress().toString());
        WebSocketConstant.group.add(ctx.channel());
        System.out.println("客户端与服务端连接开启");
    }

    /**
     * channelRead方法中调用了messageReceived，处理连接
     * web-socket初次连接时，发送的是http连接，之后会升级为webSocket连接
     */
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        // Http接入
        if (msg instanceof FullHttpRequest) {
            new NettyHttpService(nettyConfig).dealRequest(channelHandlerContext,(FullHttpRequest) msg);
        }
        // WebSocket接入
        else if (msg instanceof WebSocketFrame) {
            new NettySocketService().dealRequest(channelHandlerContext,(WebSocketFrame) msg);
        }
    }

    /**
     * 每当服务端收到新的客户端连接时,客户端的channel存入ChannelGroup列表中,并通知列表中其他客户端channel
     * 建立握手
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //获取连接的channel
        Channel channel = ctx.channel();
    }


    /**
     * 握手取消
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        System.out.println(
                "[" + ctx.channel().remoteAddress() + "]" + "exit the room");
        ctx.writeAndFlush("[" + ctx.channel().remoteAddress() + "]" + "exit the room");
        ctx.close().sync();
        ctx.fireExceptionCaught(cause);
    }

    /**
     *  断开连接时进入此方法
     * */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        WebSocketConstant.group.remove(ctx.channel());
        System.out.println("客户端与服务端连接关闭");
    }
}
