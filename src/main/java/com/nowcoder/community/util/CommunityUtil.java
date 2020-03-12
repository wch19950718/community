package com.nowcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import com.sun.mail.smtp.DigestMD5;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
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

    /**
     * 封装JSON字符串
     * @param code 提示代码（0代表成功，403代表无权限等）
     * @param msg 提示信息
     * @param map 业务数据
     * @return
     */
    public static String getJSONString(int code, String msg, Map<String,Object> map){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if(map != null){
            for(String key: map.keySet()){
                json.put(key,map.get(key));
            }
        }
        return json.toJSONString();
    }

    //msg和业务数据不一定会有，重载两个方法
    public static String getJSONString(int code,String msg){

        return getJSONString(code,msg,null);
    }
    public static String getJSONString(int code){
        return getJSONString(code,null,null);
    }


//    public static void main(String[] args) {
//        int code = 0;
//        String msg = "成功";
//        Map<String,Object> map = new HashMap<>();
//        map.put("name","万成浩");
//        map.put("age",25);
//        System.out.println(getJSONString(code,msg,map));
//    }
}
