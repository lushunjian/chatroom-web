package cn.lsj.netty.filter;

import cn.lsj.netty.handler.NettyHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @Auther: Lushunjian
 * @Date: 2018/8/21 22:55
 * @Description:
 */
public class NettyServerFilter extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline ph = ch.pipeline();
        //第一个参数为自定义的名称
        // 以("\n")为结尾分割的 解码器
/*        ph.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        // 字符串解码和编码，应和客户端一致
        ph.addLast("decoder", new StringDecoder());
        ph.addLast("encoder", new StringEncoder());*/

        //http请求，过滤器设置,webSocket是以http发起请求的
        // 编码,http服务器端对response编码
        ph.addLast("http-encoder", new HttpResponseEncoder());
        // 解码,http服务器端对request解码
        ph.addLast("http-decoder",new HttpServerCodec());
        // 目的是将多个消息转换为单一的request或者response对象，该Handler必须放在HttpServerCodec的后面
        ph.addLast("http-aggregator",new HttpObjectAggregator(65536));
        // 支持异步大文件传输，文件分块
        ph.addLast("http-block",new ChunkedWriteHandler());
        //用于处理websocket, /ws为访问websocket时的uri
        // 服务端业务逻辑
        ph.addLast("http-handler", new NettyHandler());

    }
}
