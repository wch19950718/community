package com.nowcoder.community.controller;

import com.fasterxml.jackson.databind.deser.std.MapDeserializer;
import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.nowcoder.community.service.AlphaService;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


@Controller
@RequestMapping("/alpha")
public class AlphaController {
    @Autowired
    private AlphaService alphaService;


    @RequestMapping("hello")
    @ResponseBody
    public String sayHello(){
        return "Hello Spring Boot";
    }

    @RequestMapping("data")
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }


    @RequestMapping("/http")
//    http通信底层实现
    public void http(HttpServletRequest request,HttpServletResponse response){
//      请求
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumeration = request.getHeaderNames();
        while(enumeration.hasMoreElements()){
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name+":"+value);
        }
        System.out.println(request.getParameter("code"));
//      响应
        response.setContentType("text/html;charset=utf-8");
        try(
                PrintWriter writer = response.getWriter();
        ) {
            writer.write("<h1>牛客网</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    GET请求处理（？current=1&limit=20)
    @RequestMapping(path = "/students",method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name="current",required = false,defaultValue = "1") int current,
            @RequestParam(name="limit",required = false,defaultValue = "10")int limit){
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

//    GET请求，路径参数（/student/{id})
    @RequestMapping(path = "/student/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(String id){
        System.out.println(id);
        return "a student";

    }

//    POST请求（向服务器提交数据）
    @RequestMapping(path = "/student",method = RequestMethod.POST)
    @ResponseBody
    public String postTeacher(String name,String age){
        System.out.println(name);
        System.out.println(age);
        return "success";

    }

//    响应（ModelAndView）
    @RequestMapping(path = "/teacher",method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name","张三");
        modelAndView.addObject("age","23");
        modelAndView.setViewName("/demo/view");
        return modelAndView;
    }
//    响应（返回view路径）
    @RequestMapping(path = "/school",method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name","西安电子科技大学");
        model.addAttribute("age","90");
        return "/demo/view";

    }

//    响应JSON数据
    @RequestMapping(path = "/emp",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getEmp(){
        Map<String,Object> map = new HashMap<>();
        map.put("name","张三");
        map.put("salary",8000);

        return map;

    }

    //    响应JSON数据
    @RequestMapping(path = "/emps",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getEmps(){
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        map.put("name","张三");
        map.put("salary",8000);
        list.add(map);

        map = new HashMap<>();
        map.put("name","赵四");
        map.put("salary",9000);
        list.add(map);

        map = new HashMap<>();
        map.put("name","王五");
        map.put("salary",10000);
        list.add(map);

        return list;

    }

    @RequestMapping(path="/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        //有效路径（我在这个路径和他的子路径下才会生效
        cookie.setPath("/community/alpha");
        //生存期限（默认关掉浏览器就消失（存在内存），设置时间可以在时间范围内存在，存在硬盘里）
        cookie.setMaxAge(60*10);

        response.addCookie(cookie);
        return "set cookie";
    }

    @RequestMapping(path="/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code){
        System.out.println(code);
        return "get cookie";

    }

    //Session
    @RequestMapping(path = "/session/set", method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session){
        session.setAttribute("name","wanchenghao");
        session.setAttribute("age",1);
        session.setMaxInactiveInterval(60*10);
        return "set session";
    }

    @RequestMapping(path = "/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("name"));
        System.out.println(session.getAttribute("age"));
        return "get session";
    }

    @RequestMapping(path = "/ajax",method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name, int age){
        System.out.println(name);
        System.out.println(age);
        String json = CommunityUtil.getJSONString(0,"成功");
        return json;
    }

}
