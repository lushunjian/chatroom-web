package cn.lsj.netty.chat.message;

import java.io.Serializable;

/**
 * @Auther: Lushunjian
 * @Date: 2018/9/2 17:40
 * @Description:   消息封装类
 */
public class Message implements Serializable {

    private Integer id;
    // 消息发送者
    private String sender;
    // 消息接收者
    private String receiver;
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
}
