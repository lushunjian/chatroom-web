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
    private int sendTime;
    // 发送内容
    private String messageContent;
    // 消息类型
    // whisper 表示私聊， group 为群聊
    private String messageType;
    // 消息状态
    // 1 表示实时消息  ，0 表示离线消息
    private String status;
    // 服务端通知用户异地登录下线通知，默认为 0，当为 1 时，强制客户端下线
    private int isOffline = 0;

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

    public int getSendTime() {
        return sendTime;
    }

    public void setSendTime(int sendTime) {
        this.sendTime = sendTime;
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
}
