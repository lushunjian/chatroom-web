package cn.lsj.util;

import io.netty.buffer.ByteBuf;

/**
 * @Auther: Lushunjian
 * @Date: 2018/9/8 12:06
 * @Description:
 */
public class ByteBufUtil {

    public static String byteToString(ByteBuf byteBuf){
        String str;
        if (byteBuf.hasArray()) { // 处理堆缓冲区
            str = new String(byteBuf.array(), byteBuf.arrayOffset() + byteBuf.readerIndex(), byteBuf.readableBytes());
        } else { // 处理直接缓冲区以及复合缓冲区
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.getBytes(byteBuf.readerIndex(), bytes);
            str = new String(bytes, 0, byteBuf.readableBytes());
        }
        return str;
    }
}
