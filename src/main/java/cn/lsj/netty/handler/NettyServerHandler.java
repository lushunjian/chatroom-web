package cn.lsj.netty.handler;

import cn.lsj.netty.service.NettyHttpHandler;
import cn.lsj.netty.service.NettySocketHandler;
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
 * @Date: 2018/8/21 22:55
 * @Description:
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<FullHttpRequest > {

    public static Map<String,ChannelHandlerContext> channelMap = new HashMap<String,ChannelHandlerContext>();

    /**
     * 接收客户端发送的消息 channel 通道 Read 读 简而言之就是从通道中读取数据，也就是服务端接收客户端发来的数据。
     * 但是这个数据在不进行解码时它是ByteBuf类型的
     * */
    @Override
      public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // HTTP接入
         if (msg instanceof FullHttpRequest) {
                new NettyHttpHandler().dealRequest(ctx,(FullHttpRequest) msg);
            }
        // WebSocket接入
        else if (msg instanceof WebSocketFrame) {
                new NettySocketHandler().dealRequest(ctx,(WebSocketFrame) msg);
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
     * 底层调用的还是 channelRead 方法。如果重写了channelRead，不会调用messageReceived
     */
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, FullHttpRequest msg) throws Exception {
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
        for(String address : channelMap.keySet()){

            if(!address.equals(nowAddress)){
                System.out.println("发送的address："+address);
                //从Map中取出管道流上下文对象，给客户端发送数据
                ChannelHandlerContext ctx = channelMap.get(address);
                ctx.writeAndFlush(msg+"\n");
            }
        }
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
        //通知所有已经连接到服务器的客户端，有一个新的通道加入
       /* for(Channel channel:channels){
            channel.writeAndFlush("[SERVER]-"+incomming.remoteAddress()+"加入\n");
        }*/

       channelMap.put(ctx.channel().remoteAddress().toString(),ctx);
    }
}
