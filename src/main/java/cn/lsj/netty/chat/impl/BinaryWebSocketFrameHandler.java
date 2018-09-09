package cn.lsj.netty.chat.impl;

import cn.lsj.domain.FileBlock;
import cn.lsj.domain.FileMessage;
import cn.lsj.netty.chat.WebSocketFrameHandler;
import cn.lsj.netty.constant.WebSocketConstant;
import cn.lsj.util.ByteBufUtil;
import cn.lsj.util.FileMessageParse;
import cn.lsj.util.LinkQueue;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.*;

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

    private LinkQueue<FileBlock> socketFileLinkQueue ;

    // 默认是true,表示当前请求是请求报文，而不是文件。 第一次请求时是文件请求报文。
    private static boolean isFileMessage = true;
    // 分块序号
    private static int num = 1;
    // 解析的报文
    private static FileMessage fileMessage;

    public BinaryWebSocketFrameHandler(){}

    public BinaryWebSocketFrameHandler(BinaryWebSocketFrame binaryWebSocketFrame){
        this.binaryWebSocketFrame=binaryWebSocketFrame;
    }

    public LinkQueue<FileBlock> getSocketFileLinkQueue() {
        return socketFileLinkQueue;
    }

    public void setSocketFileLinkQueue(LinkQueue<FileBlock> socketFileLinkQueue) {
        this.socketFileLinkQueue = socketFileLinkQueue;
    }

    @Override
    public void webSocketHandler(ChannelHandlerContext ctx) {
       // FileBlock fileBlock = socketFileLinkQueue.poll();
       // System.out.println("二进制消息:-----"+fileBlock.getNumber()+"----"+binaryWebSocketFrame);

    /*    ByteBuf byteBuf=binaryWebSocketFrame.content();
        try(FileOutputStream outputStream=new FileOutputStream("D:\\a.txt")){
            byteBuf.readBytes(outputStream,byteBuf.capacity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        byteBuf.clear();*/
        ByteBuf byteBuf=binaryWebSocketFrame.content();
        try {
            // 第一次是请求报文
            if(isFileMessage){
                String str = ByteBufUtil.byteToString(byteBuf);
                // 解析报文
                fileMessage = FileMessageParse.messageParse(str);
                System.out.println("报文解析：");
                System.out.println(JSON.toJSONString(fileMessage));
                System.out.println();
                System.out.println(str);
                isFileMessage = false;
            }else {
                byte[] byteArray = new byte[byteBuf.capacity()];
                byteBuf.readBytes(byteArray);
                WebSocketConstant.fileOutput.write(byteArray);
                // 如果收到的文件块等于定义的，说明当前文件块是最后一个文件块，文件块全部传输完毕
                if(num==fileMessage.getFileBlockSize()){
                    //输出到文件
                    System.out.println("输出到文件！------------------");
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream( new File( "D:/123.pdf" ) ) ;
                        WebSocketConstant.fileOutput.writeTo(fileOutputStream) ;
                        fileOutputStream.flush();
                        num=1;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally{
                        try {
                            WebSocketConstant.fileOutput.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
                num++;
                System.out.println("binaryWebSocketFrame--"+num+"---"+binaryWebSocketFrame);
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
