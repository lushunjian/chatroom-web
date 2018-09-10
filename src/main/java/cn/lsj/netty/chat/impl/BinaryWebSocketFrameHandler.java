package cn.lsj.netty.chat.impl;

import cn.lsj.netty.chat.chatutil.ChatFileOutput;
import cn.lsj.vo.FileBlock;
import cn.lsj.vo.FileMessage;
import cn.lsj.netty.chat.WebSocketFrameHandler;
import cn.lsj.netty.constant.WebSocketConstant;
import cn.lsj.util.ByteBufUtil;
import cn.lsj.util.FileMessageParse;
import cn.lsj.util.LinkQueue;
import cn.lsj.vo.FileQueueBean;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Auther: Lushunjian
 * @Date: 2018/9/1 10:12
 *    webSocket 二进制流处理类
 *    通过@Component和@Scope注解，把对象交给spring容器管理。@Scope注解注入方式为：多实例注入
 *
 */
@Scope("prototype")
@Component("binary")
public class BinaryWebSocketFrameHandler extends WebSocketFrameHandler{

    private BinaryWebSocketFrame binaryWebSocketFrame;

    // 分块序号
    private static AtomicInteger num = new AtomicInteger(1);

    public BinaryWebSocketFrameHandler(){}

    public BinaryWebSocketFrameHandler(BinaryWebSocketFrame binaryWebSocketFrame){
        this.binaryWebSocketFrame=binaryWebSocketFrame;
    }

    @Override
    public void webSocketHandler(ChannelHandlerContext ctx) {
        ByteBuf byteBuf=binaryWebSocketFrame.content();
        // 获得文件相关信息
        ConcurrentMap<String,FileQueueBean> concurrentFileMap = WebSocketConstant.fileBlockMap;
        // 通道id
        String channelId = ctx.channel().id().asLongText();
        FileQueueBean fileQueueBean = concurrentFileMap.get(channelId);
        if(fileQueueBean == null)
            fileQueueBean = new FileQueueBean();
        try {
            // 第一次是请求报文，报文数据很小;不会出现粘包现象---解析报文
            if(fileQueueBean.isFileMessage()){
                String str = ByteBufUtil.byteToString(byteBuf);
                // 解析报文
                FileMessage fileMessage = FileMessageParse.messageParse(str);
                System.out.println("报文解析完毕----"+JSON.toJSONString(fileMessage));
                // 获取发送者的用户账号，此字段请求报文中必填，否则不进行后续操作
                String userAccount = fileMessage.getParam().get("senderAccount");
                // 保存用户账号
                fileQueueBean.setUserAccount(userAccount);
                // 保存通道id
                fileQueueBean.setChannelId(channelId);
                // 获取文件名的 md5值
                String fileNameMD5 = fileMessage.getFileNameMD5();
                //先返回值,然后在+1,相当于i++
                fileQueueBean.getFileQueueCount().getAndIncrement();
                // 保存文件报文信息
                fileQueueBean.getFileMessageMap().put(fileNameMD5,fileMessage);
                // 生成文件二进制流缓存
                fileQueueBean.getFileOutputMap().put(fileNameMD5,new ByteArrayOutputStream());
                // 文件块上传队列。 队列中格式如下
                /**
                 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
                 *  dataBlock |  dataBlock |  dataBlock |  dataBlock | endBlock
                 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
                 * */
                LinkQueue<FileBlock> fileBlockLinkQueue = new LinkQueue<>();
                for(int i=0;i<fileMessage.getFileBlockSize();i++){
                    fileBlockLinkQueue.add(new FileBlock(fileNameMD5,i,userAccount));
                }
                // 加入结束标志 block
                fileBlockLinkQueue.add(new FileBlock(fileNameMD5,true,userAccount));
                // 保存文件上传队列
                fileQueueBean.setFileQueue(fileBlockLinkQueue);
                fileQueueBean.setFileMessage(false);
                concurrentFileMap.put(channelId,fileQueueBean);
            }else {
                ChatFileOutput.fileOutput(binaryWebSocketFrame,byteBuf,fileQueueBean);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public BinaryWebSocketFrame getBinaryWebSocketFrame() {
        return binaryWebSocketFrame;
    }

    public void setBinaryWebSocketFrame(BinaryWebSocketFrame binaryWebSocketFrame) {
        this.binaryWebSocketFrame = binaryWebSocketFrame;
    }

    /**
     *      Content-Type:multipart/file
     *      Accept-Encoding:utf-8
     *      File-Length:
     *      File-Block-Size:
     *      File-Name:
     *      Param-Boundary:--asvc
     *      --asvc
     *      name="senderAccount"
     *      111
     *
     *      --asvc
     *      name="receiverAccount"
     *      222
     *
     *      --asvc
     *      name="sendTime"
     *      131231313123
     *
     *      --asvc
     *
     */

}
