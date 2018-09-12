package cn.lsj.vo;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: Lushunjian
 * @Date: 2018/9/8 21:09
 * @Description:   文件上传请求报文解析bean
 */
public class FileMessage {

    // 文件唯一标识
    private String fileUuid;
    private String contentType;
    private String acceptEncoding;
    private long fileLength;
    private int fileBlockSize;
    private String fileName;
    private String paramBoundary;
    private String senderAccount;
    private String receiverAccount;
    private long sendTime;

    public String getSenderAccount() {
        return senderAccount;
    }

    public void setSenderAccount(String senderAccount) {
        this.senderAccount = senderAccount;
    }

    public String getReceiverAccount() {
        return receiverAccount;
    }

    public void setReceiverAccount(String receiverAccount) {
        this.receiverAccount = receiverAccount;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getAcceptEncoding() {
        return acceptEncoding;
    }

    public void setAcceptEncoding(String acceptEncoding) {
        this.acceptEncoding = acceptEncoding;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public int getFileBlockSize() {
        return fileBlockSize;
    }

    public void setFileBlockSize(int fileBlockSize) {
        this.fileBlockSize = fileBlockSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getParamBoundary() {
        return paramBoundary;
    }

    public void setParamBoundary(String paramBoundary) {
        this.paramBoundary = paramBoundary;
    }

    public String getFileUuid() {
        return fileUuid;
    }

    public void setFileUuid(String fileUuid) {
        this.fileUuid = fileUuid;
    }
}
