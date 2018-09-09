package cn.lsj.util;

import cn.lsj.vo.FileMessage;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Auther: Lushunjian
 * @Date: 2018/9/8 20:57
 * @Description:  文件上传请求头报文解析工具类
 */
public class FileMessageParse {

    public static FileMessage messageParse(String str) throws IOException, NoSuchAlgorithmException {
        ByteArrayInputStream is=new ByteArrayInputStream(str.getBytes());
        BufferedReader br=new BufferedReader(new InputStreamReader(is));
        String line = null;
        FileMessage fileMessage = new FileMessage();
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        while((line = br.readLine()) != null){
            String[] kv = line.split(":");
            if(kv.length==2){
                String key = kv[0];
                String value = kv[1];
                if("Content-Type".equals(key))
                    fileMessage.setContentType(value);
                else if("Accept-Encoding".equals(key))
                    fileMessage.setAcceptEncoding(value);
                else if("File-Length".equals(key))
                    fileMessage.setFileLength(Long.parseLong(value));
                else if("File-Block-Size".equals(key))
                    fileMessage.setFileBlockSize(Integer.parseInt(value));
                else if("Param-Boundary".equals(key))
                    fileMessage.setParamBoundary(value);
                else if("File-Name".equals(key))
                    fileMessage.setFileName(value);
            }else {
                // 判断参数分割符是否和定义的一致，不一致则不解析额外参数
                if(fileMessage.getParamBoundary() != null && fileMessage.getParamBoundary().equals(line)){
                    // 连读三行
                    String line1 = br.readLine();
                    String line2 = br.readLine();
                    // 如果第二行不为空，则还没到报文结尾
                    if(line2 != null){
                        String[] param = line1.split("=");
                        fileMessage.getParam().put(param[1],line2);
                    }else {  // 如果为空则已经解析完毕
                       //  设置文件名 md5值
                        fileMessage.setFileNameMD5(MD5Utils.getMD5(fileMessage.getFileName()));
                        return fileMessage;
                    }
                    // 第三行为换行
                    br.readLine();
                }
            }
        }
        //  设置文件名 md5值
        fileMessage.setFileNameMD5(MD5Utils.getMD5(fileMessage.getFileName()));
        return fileMessage;
    }


}
