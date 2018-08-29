package cn.lsj.netty.handler;

import cn.lsj.netty.service.NettyHttpService;
import cn.lsj.netty.service.NettySocketService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: Lushunjian
 * @Date: 2018/8/29 22:50
 * @Description:
 */
public class NettySocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    public static Map<String,ChannelHandlerContext> channelMap = new HashMap<String,ChannelHandlerContext>();

    /**
     * 接收客户端发送的消息 channel 通道 Read 读 简而言之就是从通道中读取数据，也就是服务端接收客户端发来的数据。
     * 但是这个数据在不进行解码时它是ByteBuf类型的
     * */
/*    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // HTTP接入
        if (msg instanceof FullHttpRequest) {
            new NettyHttpService().dealRequest(ctx,(FullHttpRequest) msg);
        }
        // WebSocket接入
        else if (msg instanceof WebSocketFrame) {
            new NettySocketService().dealRequest(ctx,(WebSocketFrame) msg);
        }
    }*/
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
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, WebSocketFrame msg) throws Exception {
        // 收到消息直接打印输出
        new NettySocketService().dealRequest(channelHandlerContext, msg);
    }

    /**
     * 每当服务端收到新的客户端连接时,客户端的channel存入ChannelGroup列表中,并通知列表中其他客户端channel
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //获取连接的channel
        Channel channel = ctx.channel();

        channelMap.put(ctx.channel().remoteAddress().toString(),ctx);
    }
}
