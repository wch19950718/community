package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    public static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }


    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage,Model model){
        if(headerImage == null){
            model.addAttribute("headerError","你还没有选择图片！");
            return "/site/setting";
        }
        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf(".")+1);
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("headerError","文件格式不正确");
        }
        filename = CommunityUtil.generateUUID() + "." + suffix;
        File dest = new File(uploadPath+"/"+filename);

        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败："+e.getMessage());
            throw new RuntimeException("上传文件失败，服务器异常",e);
        }

        //更新当前头像的访问路径
        //headerUrl命名规范 http://localhost:8080/community/user/header/***.png
        String headerUrl = domain + contextPath + "/user/header/"+filename;
        User user = hostHolder.getUser();
        userService.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";
    }

    //通过头像的访问路径获取硬盘中存储的头像
    @RequestMapping(path = "/header/{filename}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response){
        //存放路径
        filename = uploadPath+"/"+filename;
        //获得文件类型
        String suffix = filename.substring(filename.lastIndexOf(".")+1);
        response.setContentType("image/"+suffix);

        //response是SpringMVC创建的，它可以自动关闭response的输出流,但是输入流是我们自己创建的就需要自己关闭了
        try(
                OutputStream os = response.getOutputStream();
                FileInputStream fis = new FileInputStream(filename);
                ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b=fis.read(buffer))!=-1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败："+e.getMessage());
        }

    }

    //更新密码
    @LoginRequired
    @RequestMapping(path = "/password",method = RequestMethod.POST)
    public String updatePassword(String oldPassword, String newPassword, Model model, @CookieValue("ticket") String ticket){
        if(ticket != null){
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            int userId = loginTicket.getUserId();
            Map<String,Object> map = userService.updatePassword(userId,oldPassword,newPassword);
            if(!map.isEmpty()){
                model.addAttribute("oldPasswordMsg",map.get("oldPasswordMsg"));
                model.addAttribute("newPasswordMsg",map.get("newPasswordMsg"));
                return "/site/setting";
            }else{
                userService.logout(ticket);
                return "redirect:/login";
            }
        }
        return "redirect:/login";
    }

}