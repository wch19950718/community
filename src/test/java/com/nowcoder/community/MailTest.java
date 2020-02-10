package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {

    @Autowired
    private MailClient mailClient;

    @Test
    public void testTextMail(){
        mailClient.sendMail("wch526726423@qq.com","TextTest","Welcome!");
    }

    //在MVC框架下我只需要构建Model返回路径，你需要主动获取网页模板
    @Autowired
    private TemplateEngine templateEngine;
    @Test
    public void testHtmlMail(){
        //因为没有Model，你需要主动传参，就是用下面这个Context
        Context context = new Context();
        context.setVariable("username","万成浩");
        //给你获取的模板引擎传参和给出模板路径
        String content = templateEngine.process("/mail/mailDemo",context);

        System.out.println(content);
        mailClient.sendMail("wch19950718@outlook.com","HtmlTest",content);
    }
}
