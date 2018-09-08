package cn.lsj.domain;

/**
 * @Auther: Lushunjian
 * @Date: 2018/9/8 00:09
 * @Description:
 */
public class FileBlock {

    // 所属文件唯一标识
    private String uuid;
    // 当前块大小
    private long size;
    // 当前文件块序号
    private int number;

    public FileBlock(String uuid, int number) {
        this.uuid = uuid;
        this.number = number;
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
}
