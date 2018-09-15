package cn.lsj.netty.chat.chatutil;

import cn.lsj.domain.Message;
import cn.lsj.netty.constant.WebSocketConstant;
import cn.lsj.util.ByteBufUtil;
import cn.lsj.util.FileMessageParse;
import cn.lsj.util.LinkQueue;
import cn.lsj.vo.FileBlock;
import cn.lsj.vo.FileMessage;
import cn.lsj.vo.FileQueueBean;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

public class ChatFileOutput {

    private static Logger logger = LoggerFactory.getLogger(ChatFileOutput.class);

    public static void fileOutput(WebSocketFrame webSocketFrame, ByteBuf byteBuf, FileQueueBean fileQueueBean) {
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
                    logger.info("输出到文件！");
                    try {
                        outputStream.close();
                        // 获取接收端的用户账号
                        String receiverAccount=fileMessage.getReceiverAccount();
                        // 获取客户端socket通道
                        Channel channel = WebSocketConstant.concurrentMap.get(receiverAccount);
                        // 判断接收端是否在线
                        if(channel != null){
                            Message message = new Message(0);
                            message.setSender(fileMessage.getSenderAccount());
                            message.setSenderName(fileMessage.getSenderName());
                            message.setReceiver(fileMessage.getReceiverAccount());
                            message.setSendTime(fileMessage.getSendTime()+"");
                            message.setFileName(fileMessage.getFileName());
                            message.setDownloadPath(fileMessage.getFileSavePath());
                            TextWebSocketFrame content = new TextWebSocketFrame(JSON.toJSONString(message));
                            channel.writeAndFlush(content);
                        }else {
                            logger.info("处理离线消息!");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        logger.error("关流失败!!");
                    }finally {
                        // 文件块重置为0
                        fileQueueBean.getCurrentBlockNum().getAndSet(0);
                        //fileQueueBean.setFileMessage(true);
                    }
                }else {
                    logger.error("解析文件出错!");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("后台异常!!!");
        }
    }

    // 解析报，并将二进制流输出到文件
    public static void parseMessage(WebSocketFrame webSocketFrame,ChannelHandlerContext ctx){
        ByteBuf byteBuf=webSocketFrame.content();
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
                String userAccount = fileMessage.getSenderAccount();
                // 保存用户账号
                fileQueueBean.setUserAccount(userAccount);
                // 保存通道id
                fileQueueBean.setChannelId(channelId);
                // 获取文件名的 md5值
                String fileUuid = fileMessage.getFileUuid();
                //先返回值,然后在+1,相当于i++
                fileQueueBean.getFileQueueCount().getAndIncrement();
                // 保存文件报文信息
                fileQueueBean.getFileMessageMap().put(fileUuid,fileMessage);
                // 生成文件二进制流缓存
                //fileQueueBean.getFileOutputMap().put(fileNameMD5,new ByteArrayOutputStream());
                // 以流追加的形式输出到文件
                File dest = new File("D://"+fileMessage.getFileName());
                fileQueueBean.getFileOutStreamMap().put(fileUuid,new FileOutputStream(dest,true));
                // 文件块上传队列。 队列中格式如下
                /**
                 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
                 *  dataBlock |  dataBlock |  dataBlock |  dataBlock | endBlock
                 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
                 * */
                LinkQueue<FileBlock> fileBlockLinkQueue = new LinkQueue<>();
                for(int i=0;i<fileMessage.getFileBlockSize();i++){
                    fileBlockLinkQueue.add(new FileBlock(fileUuid,i,userAccount));
                }
                // 加入结束标志 block
                fileBlockLinkQueue.add(new FileBlock(fileUuid,true,userAccount));
                // 保存文件上传队列
                fileQueueBean.setFileQueue(fileBlockLinkQueue);
                fileQueueBean.setFileMessage(false);
                concurrentFileMap.put(channelId,fileQueueBean);
            }else {
                ChatFileOutput.fileOutput(webSocketFrame,byteBuf,fileQueueBean);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
