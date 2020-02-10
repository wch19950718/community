package com.nowcoder.community.util;

import com.sun.mail.smtp.DigestMD5;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {
    //生成随机的字符串（salt）
    public static String generateUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }

    //md5加密
    public static String md5(String key){
        //导入的Commons lang包中的判空功能
        if(StringUtils.isBlank(key)){
            return null;
        }else{
            return DigestUtils.md5DigestAsHex(key.getBytes());//以16进制返回
        }
    }
}
