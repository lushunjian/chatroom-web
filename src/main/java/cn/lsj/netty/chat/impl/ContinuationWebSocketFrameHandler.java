package cn.lsj.netty.chat.impl;

import cn.lsj.netty.chat.WebSocketFrameHandler;
import cn.lsj.netty.constant.WebSocketConstant;
import cn.lsj.util.ByteBufUtil;
import cn.lsj.util.LinkQueue;
import cn.lsj.vo.FileBlock;
import cn.lsj.vo.FileMessage;
import cn.lsj.vo.FileQueueBean;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

/**
 * @Auther: Lushunjian
 * @Date: 2018/9/8 11:58
 * @Description:
 */
@Scope("prototype")
@Component("continuation")
public class ContinuationWebSocketFrameHandler extends WebSocketFrameHandler {

    private ContinuationWebSocketFrame continuationWebSocketFrame;

    @Override
    public void webSocketHandler(ChannelHandlerContext ctx) {
        System.out.println("continuationWebSocketFrame-----"+continuationWebSocketFrame+"此帧是否结束--"+continuationWebSocketFrame.isFinalFragment());
        ByteBuf byteBuf=continuationWebSocketFrame.content();
        ConcurrentMap<String,FileQueueBean> concurrentFileMap = WebSocketConstant.fileBlockMap;
        FileQueueBean fileQueueBean = concurrentFileMap.get(ctx.channel().id().asLongText());
        try {
            byte[] byteArray = new byte[byteBuf.capacity()];
            byteBuf.readBytes(byteArray);
            // 获取上传的文件块队列
            LinkQueue<FileBlock> blockLinkQueue = fileQueueBean.getFileQueue();
            // 获取首节点但不弹出
            FileBlock fileBlock = blockLinkQueue.peek();
            // 获取缓存字节流对象
            ByteArrayOutputStream byteArrayOutputStream = fileQueueBean.getFileOutputMap().get(fileBlock.getUuid());
            // 将文件块流保存在缓存中
            byteArrayOutputStream.write(byteArray);
            // 判断此帧是否结束，如果结束，队列中头节点出队
            if(continuationWebSocketFrame.isFinalFragment()) {
                // 头结点出队
                blockLinkQueue.poll();
                //当前处理的文件块序号加1
                fileQueueBean.getCurrentBlockNum().getAndIncrement();
            }
            // 获取文件报文
            FileMessage fileMessage = fileQueueBean.getFileMessageMap().get(fileBlock.getUuid());
            if(fileQueueBean.getCurrentBlockNum().get() ==  fileMessage.getFileBlockSize()){
                // 取出下一个block ，正常情况，这个block是结束标志 block
                FileBlock endBlock = blockLinkQueue.poll();
                if(endBlock.isFinish()){
                    System.out.println("输出到文件！----------ContinuationWebSocketFrameHandler--------");
                    FileOutputStream fileOutputStream = null;
                    try {
                        fileOutputStream = new FileOutputStream(new File("D:/123.pdf"));
                        byteArrayOutputStream.writeTo(fileOutputStream);
                        fileOutputStream.flush();
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        // 重置为01
                        fileQueueBean.getCurrentBlockNum().getAndSet(0);
                        fileQueueBean.setFileMessage(true);
                        try {
                            byteArrayOutputStream.close();
                            if(fileOutputStream != null)
                                fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    System.out.println("解析文件出错!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ContinuationWebSocketFrame getContinuationWebSocketFrame() {
        return continuationWebSocketFrame;
    }

    public void setContinuationWebSocketFrame(ContinuationWebSocketFrame continuationWebSocketFrame) {
        this.continuationWebSocketFrame = continuationWebSocketFrame;
    }
}
