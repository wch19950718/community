package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {
    @Autowired
    private UserService userService;

    @RequestMapping(path = "/login" , method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }


    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String,Object> map = userService.register(user);
        if( map==null || map.isEmpty()){
            model.addAttribute("message","注册成功，请查看邮箱进行激活！");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }
        model.addAttribute("usernameMsg",map.get("usernameMsg"));
        model.addAttribute("passwordMsg",map.get("passwordMsg"));
        model.addAttribute("emailMsg",map.get("emailMsg"));
        return "/site/register";
    }

    @RequestMapping(path="/activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId,@PathVariable("code") String code){

        int result = userService.activation(userId,code);
        if(result == ACTIVATION_SUCCESS){
            model.addAttribute("message","激活成功，请查看邮箱进行激活！");
            model.addAttribute("target","/login");
        }else if(result == ACTIVATION_REPEAT){
            model.addAttribute("message","无效操作,该账号已经激活过了!");
            model.addAttribute("target","/index");
        }else{
            model.addAttribute("message","激活失败,您提供的激活码不正确!");
            model.addAttribute("target","/index");
        }

        return "/site/operate-result";
    }


}