package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.ognl.ObjectElementsAccessor;
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

    @Autowired
    private LoginTicketMapper loginTicketMapper;


    public User findUserById(int userId){

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

    //登录业务处理，主要包含了了对用户账号密码的登录处理，验证码在视图层处理
    //封装账户密码相关错误信息，如果没有错误则产生LoginTicket
    public Map<String,Object> login(String username,String password,int expired){
        Map<String, Object> map =new HashMap<>();
        //对进来的账号密码进行判空处理
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空！");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空");
            return map;
        }

        //验证账号密码
        User user = userMapper.selectByName(username);
        if(user == null){
            map.put("usernameMsg","账号不存在！");
            return map;
        }
        if(user.getStatus()==0){
            map.put("usernameMsg","账号未激活！");
            return map;
        }
        password = CommunityUtil.md5(password+user.getSalt());
        if(!password.equals(user.getPassword())){
            map.put("passwordMsg","密码不正确！");
            return map;
        }

        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        String ticket = CommunityUtil.generateUUID();
        loginTicket.setTicket(ticket);
        loginTicket.setStatus(0);
        long expiredMilliSeconds = ((long)expired)*1000;
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredMilliSeconds));
        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket",ticket);
        return map;


    }

    public void logout(String ticket){
        loginTicketMapper.updateLoginTicket(ticket,1);
    }

    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectLoginTicket(ticket);
    }

    public int updateHeader(int userId, String headerUrl){
        return userMapper.updateHeader(userId,headerUrl);
    }

    public Map<String,Object> updatePassword(int userId,String oldPassword, String newPassword){
        Map<String,Object> map = new HashMap<>();
        if(StringUtils.isBlank(oldPassword)){
            map.put("oldPasswordMsg","原密码不能为空！");
            return map;
        }
        if(StringUtils.isBlank(newPassword)){
            map.put("newPasswordMsg","新密码不能为空！");
            return map;
        }
        User user = userMapper.selectById(userId);
        oldPassword = CommunityUtil.md5(oldPassword+user.getSalt());
        newPassword = CommunityUtil.md5(newPassword+user.getSalt());
        if(!oldPassword.equals(user.getPassword())){
            map.put("oldPasswordMsg","原密码错误！");
            return map;
        }
        if(oldPassword.equals(newPassword)){
            map.put("newPasswordMsg","新密码不能和原密码相同!");
            return map;
        }

        userMapper.updatePassword(userId,newPassword);
        return map;



    }

    public User findUserByName(String name){
        return userMapper.selectByName(name);
    }



}
