package cn.lsj.netty;

import cn.lsj.netty.config.NettyConfig;
import cn.lsj.netty.handler.NettyServerFilter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

@Component
public class NettyServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

    /**
     * NETT服务器配置类
     */
    @Resource
    private NettyConfig nettyConfig;

    private EventLoopGroup boss=null;

    private EventLoopGroup work=null;

    /**
     * 关闭服务器方法，在类对象销毁之前执行
     */
    @PreDestroy
    public void close() {
        //优雅退出
        if(boss != null && work != null) {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }

    /**
     * 开启及服务线程
     */
    public void start() {
        try {
            int port = nettyConfig.getPort();
            //通过nio方式来接收连接和处理连接
            boss = new NioEventLoopGroup(nettyConfig.getBossThreadCount());
            work = new NioEventLoopGroup(nettyConfig.getWorkThreadCount());
            //创建bootstrap
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                   // .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new NettyServerFilter());

            ChannelFuture f = bootstrap.bind(port).sync();
            LOGGER.info("Netty started on port(s): {} (socket)", port);
            // 监听服务器关闭监听
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            if(boss != null && work != null) {
                boss.shutdownGracefully();
                work.shutdownGracefully();
            }
        }
    }
}
