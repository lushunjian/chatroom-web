package cn.lsj.netty.chat.chatutil;

import cn.lsj.util.LinkQueue;
import cn.lsj.vo.FileBlock;
import cn.lsj.vo.FileMessage;
import cn.lsj.vo.FileQueueBean;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ChatFileOutput {

    public static void fileOutput(WebSocketFrame webSocketFrame, ByteBuf byteBuf, FileQueueBean fileQueueBean) throws IOException {
        // 用户文件块传输开始
        byte[] byteArray = new byte[byteBuf.capacity()];
        byteBuf.readBytes(byteArray);
        // 获取上传的文件块队列
        LinkQueue<FileBlock> blockLinkQueue = fileQueueBean.getFileQueue();
        // 获取首节点但不弹出
        FileBlock fileBlock = blockLinkQueue.peek();
        // 获取缓存字节流对象
        ByteArrayOutputStream byteArrayOutputStream = fileQueueBean.getFileOutputMap().get(fileBlock.getUuid());
        // 获取文件报文
        FileMessage fileMessage = fileQueueBean.getFileMessageMap().get(fileBlock.getUuid());
        // 将文件块流保存在缓存中
        byteArrayOutputStream.write(byteArray);
        // 判断此帧是否结束，如果结束，队列中头节点出队
        if(webSocketFrame.isFinalFragment()) {
            // 头结点出队
            blockLinkQueue.poll();
            //当前处理的文件块序号加1
            fileQueueBean.getCurrentBlockNum().getAndIncrement();
        }
        // 如果当前处理的文件块序号与文件块总数相同，说明已处理完最后一个文件块
        if(fileQueueBean.getCurrentBlockNum().get() ==  fileMessage.getFileBlockSize()){
            // 取出下一个block ，正常情况，这个block是结束标志 block
            FileBlock endBlock = blockLinkQueue.poll();
            // 如果是结束块标识，表示所有文件块已经上传完毕
            if(endBlock.isFinish()){
                System.out.println("输出到文件！-------------BinaryWebSocketFrameHandler-----");
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(new File("D:/123.pdf"));
                    byteArrayOutputStream.writeTo(fileOutputStream);
                    fileOutputStream.flush();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    // 重置为0
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
    }
}
