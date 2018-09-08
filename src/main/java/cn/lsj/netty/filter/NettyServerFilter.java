package cn.lsj.netty.filter;

import cn.lsj.netty.config.NettyConfig;
import cn.lsj.netty.handler.NettyHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: Lushunjian
 * @Date: 2018/8/21 22:55
 * @Description:
 */
@Component
public class NettyServerFilter extends ChannelInitializer<SocketChannel> {

    @Autowired
    NettyHandler nettyHandler;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline ph = ch.pipeline();


/*        // 以("\n")为结尾分割的 解码器
        ph.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        // 字符串解码和编码，应和客户端一致
        ph.addLast("decoder", new StringDecoder());
        ph.addLast("encoder", new StringEncoder());

        // 编码,http服务器端对response编码
        //ph.addLast("http-encoder", new HttpResponseEncoder());
        // 解码,http服务器端对request解码 */


        ph.addLast("http-decoder",new HttpServerCodec());
        // 目的是将多个消息转换为单一的request或者response对象，该Handler必须放在HttpServerCodec的后面
        ph.addLast("http-aggregator",new HttpObjectAggregator(65536));
        // 支持异步大文件传输，文件分块
        ph.addLast("http-block",new ChunkedWriteHandler());
        // 文件分块大小
        ph.addLast("fix-length",new FixedLengthFrameDecoder(32768));
        // 服务端业务逻辑处理类
        //ph.addLast("http-handler", new NettyHandler(nettyConfig));
        // 通过spring 注入
        ph.addLast("http-handler", nettyHandler);

    }
}
