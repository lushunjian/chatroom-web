package cn.lsj.domain;

import java.io.Serializable;

/**
 * @Auther: Lushunjian
 * @Date: 2018/9/2 17:40
 * @Description:   消息封装类
 */
public class Message implements Serializable {

    public Message(){}

    public Message(int isOffline){
        this.isOffline=isOffline;
    }

    private Integer id;
    // 消息发送者账号
    private String sender;
    // 消息发送者姓名
    private String senderName;
    // 消息接收者账号
    private String receiver;
    // 消息接收者姓名
    private String receiverName;
    // 发送时间，毫秒数
    private String sendTime;
    // 发送内容
    private String messageContent;
    // 消息类型
    // text -- 文本消息; file -- 文件消息; video -- 视频消息; audio -- 语音消息
    private String messageType;
    // 消息状态
    // 1 表示实时消息  ，0 表示离线消息
    private String status;
    // 服务端通知用户异地登录下线通知，默认为 0，当为 1 时，强制客户端下线
    private int isOffline = 0;
    // 消息是否包含附件 默认为 0，表示不含，当为 1 时，表示包含文件，
    private int haveFileMessage = 0;
    // 文件信息,文件名
    private String fileName;
    // 文件大小
    private long fileSize;
    // 文件下载路径
    private String downloadPath;
    // 视频请求是否同意，状态   -- accept   -- reject  -- pending
    private String videoRequest;
    // 视频请求前端
    private Object sdp;
    // candidate
    private Object candidate;
    // 视频请求类型  -- offer   --- answer  -- ice_candidate
    private String event;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public int getIsOffline() {
        return isOffline;
    }

    public void setIsOffline(int isOffline) {
        this.isOffline = isOffline;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public int getHaveFileMessage() {
        return haveFileMessage;
    }

    public void setHaveFileMessage(int haveFileMessage) {
        this.haveFileMessage = haveFileMessage;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getVideoRequest() {
        return videoRequest;
    }

    public void setVideoRequest(String videoRequest) {
        this.videoRequest = videoRequest;
    }

    public Object getSdp() {
        return sdp;
    }

    public void setSdp(Object sdp) {
        this.sdp = sdp;
    }

    public Object getCandidate() {
        return candidate;
    }

    public void setCandidate(Object candidate) {
        this.candidate = candidate;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
