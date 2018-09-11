package cn.lsj.netty.chat.chatutil;

import cn.lsj.util.LinkQueue;
import cn.lsj.vo.FileBlock;
import cn.lsj.vo.FileMessage;
import cn.lsj.vo.FileQueueBean;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.io.FileOutputStream;
import java.io.IOException;

public class ChatFileOutput {

    public static void fileOutput(WebSocketFrame webSocketFrame, ByteBuf byteBuf, FileQueueBean fileQueueBean) throws IOException {
        // 用户文件块传输开始
        byte[] byteArray = new byte[byteBuf.capacity()];
        byteBuf.readBytes(byteArray);
        try{
            // 获取上传的文件块队列
            LinkQueue<FileBlock> blockLinkQueue = fileQueueBean.getFileQueue();
            // 获取首节点但不弹出
            FileBlock fileBlock = blockLinkQueue.peek();
            // 获取文件报文
            FileMessage fileMessage = fileQueueBean.getFileMessageMap().get(fileBlock.getUuid());
            // 使用文件输出流 边读边写
            FileOutputStream outputStream = fileQueueBean.getFileOutStreamMap().get(fileBlock.getUuid());
            outputStream.write(byteArray,0,byteArray.length);
            outputStream.flush();//强制刷新出去
            // 判断此帧是否结束，如果结束，队列中头节点出队---此块代码目的是计算文件块的个数，用以判断前端的所有文件块是否都上传完毕
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
                // 如果是结束块标识，表示所有文件块已经上传完毕，关闭输出流。重置文件块下标
                if(endBlock.isFinish()){
                    System.out.println("输出到文件！-------------BinaryWebSocketFrameHandler-----fileName"+fileMessage.getFileName());
                    try {
                        outputStream.close();
                    }catch (Exception e){
                        e.printStackTrace();
                        System.out.println("关流失败!!");
                    }finally {
                        // 文件块重置为0
                        fileQueueBean.getCurrentBlockNum().getAndSet(0);
                        fileQueueBean.setFileMessage(true);
                    }
                }else {
                    System.out.println("解析文件出错!");
                }

                }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("后台异常!!!");
        }
    }
}
