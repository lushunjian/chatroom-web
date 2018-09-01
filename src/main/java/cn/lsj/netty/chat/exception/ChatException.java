package cn.lsj.netty.chat.exception;

/**
 * @Auther: Lushunjian
 * @Date: 2018/9/1 15:53
 * @Description:
 */
public class ChatException extends RuntimeException {

    private String message;

    public ChatException(String message){
        this.message=message;
    }

    public static ChatException error(String message){
        throw new ChatException(message);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
