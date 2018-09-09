package cn.lsj.vo;

import cn.lsj.util.LinkQueue;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Auther: Lushunjian
 * @Date: 2018/9/9 17:09
 * @Description:
 */
public class FileQueueBean {

    // 用于存放文件上传的文件块对象
    private LinkQueue<FileBlock> fileQueue = new LinkQueue<>();
    // 队列中当前包含的文件数量(注意是文件数量，不是文件块的数量)
    private AtomicInteger fileQueueCount = new AtomicInteger(0);
    // 每个文件的请求报文 (文件信息) ; 键是文件名的md5值
    private ConcurrentMap<String,FileMessage> fileMessageMap = new ConcurrentHashMap<>();
    // 每个文件的二进制流存储对象; 存储在内存中 ; 键是文件名的md5值
    private ConcurrentMap<String,ByteArrayOutputStream> fileOutputMap = new ConcurrentHashMap<>();
    // 当前进行到的文件块序号
    private AtomicInteger currentBlockNum = new AtomicInteger(0);
    // 客户端channelId
    private String channelId;
    // 当前正在上传的用户
    private String userAccount;
    // 默认是true,表示当前请求是请求报文，而不是文件。 第一次请求时是文件请求报文。
    private boolean isFileMessage = true;

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public boolean isFileMessage() {
        return isFileMessage;
    }

    public void setFileMessage(boolean fileMessage) {
        isFileMessage = fileMessage;
    }

    public LinkQueue<FileBlock> getFileQueue() {
        return fileQueue;
    }

    public void setFileQueue(LinkQueue<FileBlock> fileQueue) {
        this.fileQueue = fileQueue;
    }


    public ConcurrentMap<String, FileMessage> getFileMessageMap() {
        return fileMessageMap;
    }

    public void setFileMessageMap(ConcurrentMap<String, FileMessage> fileMessageMap) {
        this.fileMessageMap = fileMessageMap;
    }

    public ConcurrentMap<String, ByteArrayOutputStream> getFileOutputMap() {
        return fileOutputMap;
    }

    public void setFileOutputMap(ConcurrentMap<String, ByteArrayOutputStream> fileOutputMap) {
        this.fileOutputMap = fileOutputMap;
    }

    public AtomicInteger getFileQueueCount() {
        return fileQueueCount;
    }

    public void setFileQueueCount(AtomicInteger fileQueueCount) {
        this.fileQueueCount = fileQueueCount;
    }

    public AtomicInteger getCurrentBlockNum() {
        return currentBlockNum;
    }

    public void setCurrentBlockNum(AtomicInteger currentBlockNum) {
        this.currentBlockNum = currentBlockNum;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
}
