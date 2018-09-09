package cn.lsj.vo;

/**
 * @Auther: Lushunjian
 * @Date: 2018/9/8 00:09
 * @Description:
 */
public class FileBlock {

    // 所属文件唯一标识
    private String uuid;
    // 所属用户账号
    private String sendAccount;
    // 当前块大小
    private long size;
    // 当前文件块序号
    private int number;
    // 当前文件块是否接收完毕
    // 由于tcp粘包的原因  会出现当前的二进制不是完整的一个数据帧(BinaryWebSocketFrame);
    // 如果不是完整的帧紧随其后的就是此帧剩下的数据(ContinuationWebSocketFrame);
    // 结束标志
    private boolean isFinish = false;

    public FileBlock(String uuid, int number, String sendAccount) {
        this.uuid = uuid;
        this.number = number;
        this.sendAccount = sendAccount;
    }

    public FileBlock(String uuid, boolean isFinish, String sendAccount) {
        this.uuid = uuid;
        this.isFinish = isFinish;
        this.sendAccount = sendAccount;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    public String getSendAccount() {
        return sendAccount;
    }

    public void setSendAccount(String sendAccount) {
        this.sendAccount = sendAccount;
    }
}
