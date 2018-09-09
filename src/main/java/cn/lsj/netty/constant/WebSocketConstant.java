package cn.lsj.netty.constant;

import cn.lsj.vo.FileBlock;
import cn.lsj.util.LinkQueue;
import cn.lsj.vo.FileQueueBean;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WebSocketConstant {

    public static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static WebSocketServerHandshaker serverHandshake;      //握手成功对象，用于关闭连接
    // 用于保存用户连接的管道上下文对象
    public static ConcurrentMap<String,Channel> concurrentMap = new ConcurrentHashMap<>();
    public final static String WEBSOCKET= "websocket";
    public final static String GET= "GET";
    public final static String HOST= "Host";
    public final static String CONNECTION= "Connection";
    public final static String UPGRADE= "Upgrade";
    public final static String SEC_WEBSOCKET_VERSION= "Sec-WebSocket-Version";
    public final static String SEC_WEBSOCKET_KEY= "Sec-WebSocket-Key";
    public static String WEB_SOCKET_URL="ws:///%s:/%d/%s";
    // 私聊
    public static String WHISPER="whisper";
    // 群聊
    public static String GROUP="group";
    // 文件上传队列
    public static LinkQueue<FileBlock> socketFileLinkQueue = new LinkQueue<>();
    // 用户的文件上传信息,键是 channelId
    public static ConcurrentMap<String,FileQueueBean> fileBlockMap = new ConcurrentHashMap<>();

}
