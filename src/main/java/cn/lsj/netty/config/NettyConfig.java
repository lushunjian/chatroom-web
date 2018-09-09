package cn.lsj.netty.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Auther: Lushunjian
 * @Date: 2018/8/21 22:55
 * @Description:
 */
@Component
@ConfigurationProperties(prefix = "netty")
public class NettyConfig {

    private String host;

    private String route;

    private int port;

    private int bossThreadCount;

    private int workThreadCount;

    private int maxFramePayloadLength;

    public int getWorkThreadCount() {
        return workThreadCount;
    }

    public void setWorkThreadCount(int workThreadCount) {
        this.workThreadCount = workThreadCount;
    }

    public int getBossThreadCount() {
        return bossThreadCount;
    }

    public void setBossThreadCount(int bossThreadCount) {
        this.bossThreadCount = bossThreadCount;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public int getMaxFramePayloadLength() {
        return maxFramePayloadLength;
    }

    public void setMaxFramePayloadLength(int maxFramePayloadLength) {
        this.maxFramePayloadLength = maxFramePayloadLength;
    }
}
