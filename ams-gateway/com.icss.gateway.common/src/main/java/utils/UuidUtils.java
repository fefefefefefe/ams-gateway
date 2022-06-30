package utils;

import java.util.UUID;

/**
 *@author Bryan Wang
 *@date 2019年9月16日
 */
public class UuidUtils {
    /**
     * 自动生成32位的UUid，对应数据库的主键id进行插入用。
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    public static void main(String[] args){
        System.out.println("===>"+getUUID());
    }
}
