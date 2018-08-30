package cn.lsj.netty.handler;

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


    private WebSocketServerHandshaker serverShakeHand;

    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static Map<String,ChannelHandlerContext> channelMap = new HashMap<String,ChannelHandlerContext>();

    /**
     * 接收客户端发送的消息 channel 通道 Read 读 简而言之就是从通道中读取数据，也就是服务端接收客户端发来的数据。
     * 但是这个数据在不进行解码时它是ByteBuf类型的
     * */
     @Override
      public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // new NettyHttpService().dealRequest(ctx,(FullHttpRequest) msg,serverShakeHand);
        // HTTP接入
         if (msg instanceof FullHttpRequest) {
                new NettyHttpService().dealRequest(ctx,(FullHttpRequest) msg,serverShakeHand);
            }
        // WebSocket接入
        else if (msg instanceof WebSocketFrame) {
                new NettySocketService().dealRequest(ctx,(WebSocketFrame) msg,serverShakeHand);
            }
      }
    /*
     * 建立连接时，返回消息
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接的客户端地址:" + ctx.channel().remoteAddress().toString());
        ctx.writeAndFlush(" connect success! [ client host name:"+ InetAddress.getLocalHost().getHostName() + " ]\n");
        super.channelActive(ctx);
    }

    /**
     * channelRead方法中调用了channelRead
     */
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        //new NettyHttpService().dealRequest(channelHandlerContext, msg, serverShakeHand);
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
        channels.add(channel);
    }


    /**
     * 握手取消
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        channels.remove(incoming);
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
        Channel channel = ctx.channel();
        System.out.println("[" + channel.remoteAddress() + "] " + "offline");
        ctx.writeAndFlush("[" + ctx.channel().remoteAddress() + "]" + "offline");
    }
}
