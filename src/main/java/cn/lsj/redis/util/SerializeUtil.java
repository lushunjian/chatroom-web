package cn.lsj.redis.util;

import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @Auther: Lushunjian
 * @Date: 2018/8/21 22:57
 * @Description:
 */
public class SerializeUtil {

    static Logger log = Logger.getLogger(SerializeUtil.class);
    /**
     *
     * <p>Title: ObjSerialize</p>
     * <p>Description: 序列化一个对象</p>
     * @param obj
     * @return
     * @author Lushunjian
     */
    public static byte[] objectSerialize(Object obj){
        ObjectOutputStream oos = null;
        ByteArrayOutputStream byteOut = null;
        try{
            byteOut = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(byteOut);
            oos.writeObject(obj);
            return byteOut.toByteArray();
        }catch (Exception e) {
            e.printStackTrace();
            log.error("Serialize failed");
        }
        return null;
    }
    /**
     *
     * <p>Title: unSerialize</p>
     * <p>Description: 反序列化</p>
     * @param bytes
     * @return
     * @author Lushunjian
     */
    public static Object objectDeSerialize(byte[] bytes){
        ByteArrayInputStream in = null;
        try{
            in = new ByteArrayInputStream(bytes);
            ObjectInputStream objIn = new ObjectInputStream(in);
            return objIn.readObject();
        }catch (Exception e) {
            e.printStackTrace();
            log.error("Serialize failed");
        }
        return null;
    }
}
