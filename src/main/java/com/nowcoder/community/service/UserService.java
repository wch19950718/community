package com.nowcoder.community.service;

import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.AdviceName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${community.path.domain}")
    private String domain;

    public User findUser(int userId){

        return userMapper.selectById(userId);
    }

    public Map<String,Object> register(User user){
        Map<String,Object> map = new HashMap<>();
        //对空值进行处理
        if(user == null){
            throw new IllegalArgumentException("参数不能为空!");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空!");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空!");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空!");
            return map;
        }
        //验证账号（账号和email不能存在）
        User user1 = userMapper.selectByName(user.getUsername());
        if(user1 != null){
            map.put("usernameMsg","该账号已存在！");
            return map;
        }

        user1 = userMapper.selectByEmail(user.getEmail());
        if(user1 != null){
            map.put("emailMsg","该邮箱已被注册");
            return map;
        }
        String salt = CommunityUtil.generateUUID().substring(0,5);
        user.setSalt(salt);
        user.setPassword(CommunityUtil.md5(user.getPassword()+salt));
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setType(0);
        user.setStatus(0);
        user.setCreateTime(new Date());
        user.setActivationCode(CommunityUtil.generateUUID());
        userMapper.insertUser(user);

        //发送邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        String url = domain + contextPath +"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);

        return map;
    }

    public int activation(int userId,String code){
        User user = userMapper.selectById(userId);
        if(user.getStatus()==1){
            return ACTIVATION_REPEAT;
        }else if (user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }

    }

}
