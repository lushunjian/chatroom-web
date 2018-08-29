package cn.lsj.netty;

import cn.lsj.netty.config.NettyConfig;
import cn.lsj.netty.filter.NettyServerFilter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
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
            /**
             * Boss线程：由这个线程池提供的线程是boss种类的，用于创建、连接、绑定socket，
             * 然后把这些socket传给worker线程池。在服务器端每个监听的socket都 有一个boss线 程来处理。
             * 在客户端，只有一个boss线程来处理所有的socket。
             */
            boss = new NioEventLoopGroup(nettyConfig.getBossThreadCount());
            /**
             * Worker线程：Worker线 程执行所有的异步I/O，即处理操作
             */
            work = new NioEventLoopGroup(nettyConfig.getWorkThreadCount());
            //创建bootstrap
            /** ServerBootstrap 启动NIO服务的辅助启动类,负责初始话netty服务器，并且开始监听端口的socket请求 */
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(boss, work)
                    /** 设置非阻塞,用它来建立新accept的连接*/
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                   // .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new NettyServerFilter());

            /** 绑定本地端口并同步等待完成*/
            ChannelFuture f = bootstrap.bind(port).sync();
            LOGGER.info("Netty started on port(s): {} (socket)", port);
            /** 监听服务器关闭 */
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
