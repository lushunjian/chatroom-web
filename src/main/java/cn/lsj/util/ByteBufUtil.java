package cn.lsj.util;

import io.netty.buffer.ByteBuf;

import java.util.List;

/**
 * @Auther: Lushunjian
 * @Date: 2018/9/8 12:06
 * @Description:
 */
public class ByteBufUtil {

    public static String byteToString(ByteBuf byteBuf){
        StringBuilder result= new StringBuilder();
        byte[] byteArray = new byte[byteBuf.capacity()];
        byteBuf.readBytes(byteArray);
        // 去除byte中为0的元素
        for(byte b : byteArray){
            if (b != 0) {
                result.append((char) b);
            }
        }
        return result.toString();
    }

    /**
     * 将多个byte[] 合并为一个
     * */
    public static byte[] byteMergerAll(byte[]... values) {
        int length_byte = 0;
        for (int i = 0; i < values.length; i++) {
            length_byte += values[i].length;
        }
        byte[] all_byte = new byte[length_byte];
        int countLength = 0;
        for (int i = 0; i < values.length; i++) {
            byte[] b = values[i];
            System.arraycopy(b, 0, all_byte, countLength, b.length);
            countLength += b.length;
        }
        return all_byte;
    }

    /**
     * 将多个byte[] 合并为一个
     * */
    public static byte[] byteMergerAll(List<byte[]> byteList) {
        int length_byte = 0;
        for (int i = 0; i < byteList.size(); i++) {
            length_byte += byteList.get(i).length;
        }
        byte[] all_byte = new byte[length_byte];
        int countLength = 0;
        for (int i = 0; i < byteList.size(); i++) {
            byte[] b = byteList.get(i);
            System.arraycopy(b, 0, all_byte, countLength, b.length);
            countLength += b.length;
        }
        return all_byte;
    }
}
