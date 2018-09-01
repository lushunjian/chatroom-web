package cn.lsj.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetAddress;

/**
 * @Auther: Lushunjian
 * @Date: 2018/8/29 23:01
 * @Description:   测试类，仅支持文本传输 (String)
 */
public class NettyTextHandler extends SimpleChannelInboundHandler<String> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接的客户端地址:" + ctx.channel().remoteAddress().toString());
        ctx.writeAndFlush(" connect success! [ client host ip:"+ ctx.channel().remoteAddress() +"client host name:"+InetAddress.getLocalHost().getHostName()+ " ]\n");
        //ctx.writeAndFlush(" connect success! [ client host name:"+ InetAddress.getLocalHost().getHostName() + " ]\n");
        super.channelActive(ctx);
    }

    /**
     * channelRead方法中调用了channelRead
     */
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        // 收到消息直接打印输出
        System.out.println("服务端接受的消息 : " + msg);
        if("quit".equals(msg)){//服务端断开的条件
            channelHandlerContext.close();
        }
        String message = "server message get";
        // 返回客户端消息
        // channelHandlerContext.writeAndFlush(message+"\n");
        String nowAddress = channelHandlerContext.channel().remoteAddress()+"";
        System.out.println("当前的address："+nowAddress);
        channelHandlerContext.writeAndFlush("server message:"+msg);
    }

    /**
     * 每当服务端收到新的客户端连接时,客户端的channel存入ChannelGroup列表中,并通知列表中其他客户端channel
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(
                "[" + ctx.channel().remoteAddress() + "] " + "is coming");
    }

    /**
     * 连接异常时进入此方法
     * */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        System.out.println(
                "[" + ctx.channel().remoteAddress() + "]" + "exit the room");
        ctx.writeAndFlush("[" + ctx.channel().remoteAddress() + "]" + "exit the room");
        ctx.close().sync();
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
