package cn.lsj.domain;

/**
 * @Auther: Lushunjian
 * @Date: 2018/9/7 23:30
 * @Description:
 */
public class SocketFile {

    // 文件唯一表示
    private String uuid;
    // 文件名
    private String fileName;
    //  文件总大小
    private long fileSize;
    //  每块文件大小
    private long blockSize;
    // 文件分块数
    private int splitSize;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public int getSplitSize() {
        return splitSize;
    }

    public void setSplitSize(int splitSize) {
        this.splitSize = splitSize;
    }

    public long getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(long blockSize) {
        this.blockSize = blockSize;
    }
}
