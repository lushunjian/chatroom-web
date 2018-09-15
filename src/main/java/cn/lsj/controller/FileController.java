package cn.lsj.controller;

import cn.lsj.netty.constant.WebSocketConstant;
import cn.lsj.util.LinkQueue;
import cn.lsj.vo.FileBlock;
import cn.lsj.vo.FileMessage;
import cn.lsj.vo.FileQueueBean;
import cn.lsj.vo.HttpResponseBean;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ConcurrentMap;

/**
 * @Auther: Lushunjian
 * @Date: 2018/8/23 07:49
 * @Description:   文件上传处理
 */
@RestController
@RequestMapping(value = "/socket",produces = {MediaType.APPLICATION_JSON_VALUE})
public class FileController {

    @Value("${netty.filePath}")
    private String filePath;

    /**
     * socket文件互传，请求报文解析
     * */
    @ResponseBody
    @PostMapping(value = "/file/message")
    public HttpResponseBean socketFileMessage(FileMessage fileMessage){
        // 获取发送者的用户账号，此字段请求报文中必填，否则不进行后续操作
        String userAccount = fileMessage.getSenderAccount();
        // 根据当前用户获取通道对象
        try {
            if (userAccount == null || "".equals(userAccount)) {
                return null;
            } else {
                Channel channel = WebSocketConstant.concurrentMap.get(userAccount);
                // 获得文件相关信息
                if (channel == null) {        //为空则为离线文件
                    System.out.println("暂不处理离线消息");
                    return new HttpResponseBean(333);
                } else {
                    String channelId = channel.id().asLongText();
                    FileQueueBean fileQueueBean = WebSocketConstant.fileBlockMap.get(channelId);
                    // 生成文件信息对象
                    if (fileQueueBean == null)
                        fileQueueBean = new FileQueueBean();

                    // 保存用户账号
                    fileQueueBean.setUserAccount(userAccount);
                    // 保存通道id
                    fileQueueBean.setChannelId(channelId);
                    // 获取文件名的 md5值
                    String fileUUID = fileMessage.getFileUuid();
                    //先返回值,然后在+1,相当于i++
                    fileQueueBean.getFileQueueCount().getAndIncrement();
                    // 以流追加的形式输出到文件
                    String fileSavePath = filePath + fileMessage.getFileName();
                    fileMessage.setFileSavePath(fileSavePath);
                    // 保存文件报文信息
                    fileQueueBean.getFileMessageMap().put(fileUUID, fileMessage);
                    // 生成文件二进制流缓存
                    //fileQueueBean.getFileOutputMap().put(fileNameMD5,new ByteArrayOutputStream());
                    // 判断文件夹是否存在 ,如果没有，递归创建文件夹
                    File file = new File(filePath);
                    if(!file.exists()&&!file.isDirectory()) {
                        boolean flag = file.mkdirs();
                        if(!flag)    //文件夹创建失败
                            return new HttpResponseBean(500);
                    }
                    // 保存路径
                    File dest = new File(fileSavePath);
                    fileQueueBean.getFileOutStreamMap().put(fileUUID, new FileOutputStream(dest, true));
                    // 文件块上传队列。 队列中格式如下
                    /**
                     * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
                     *  dataBlock |  dataBlock |  dataBlock |  dataBlock | endBlock
                     * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
                     * */
                    LinkQueue<FileBlock> fileBlockLinkQueue = new LinkQueue<>();
                    for (int i = 0; i < fileMessage.getFileBlockSize(); i++) {
                        fileBlockLinkQueue.add(new FileBlock(fileUUID, i, userAccount));
                    }
                    // 加入结束标志 block
                    fileBlockLinkQueue.add(new FileBlock(fileUUID, true, userAccount));
                    // 保存文件上传队列
                    fileQueueBean.setFileQueue(fileBlockLinkQueue);
                    fileQueueBean.setFileMessage(false);
                    WebSocketConstant.fileBlockMap.put(channelId, fileQueueBean);
                    return new HttpResponseBean(200);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return new HttpResponseBean(500);
        }
    }
}
