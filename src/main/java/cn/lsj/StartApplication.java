package cn.lsj;

import cn.lsj.netty.NettyServer;
import cn.lsj.netty.chat.spring.ApplicationContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * @Auther: Lushunjian
 * @Date: 2018/8/21 22:55
 * @Description:
 */
@SpringBootApplication
public class StartApplication implements CommandLineRunner {

    private final NettyServer nettyServer;

    @Autowired
    public StartApplication(NettyServer nettyServer) {
        this.nettyServer = nettyServer;
    }

    public static void main(String[] args) {

        SpringApplication.run(StartApplication.class, args);

    }

    @Override
    public void run(String... strings) {
       nettyServer.start();
    }
}
