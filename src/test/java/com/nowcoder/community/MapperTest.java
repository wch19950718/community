package com.nowcoder.community;

import com.mysql.cj.protocol.MessageSender;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.*;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.setOut;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private UserMapper userMapper;


    @Test
    public void testDiscussPost(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0,0,10);
        for(DiscussPost post:list){
            System.out.println(post.toString());
        }

        int rows = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(rows);
    }
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testHome(){
        List<DiscussPost> list = discussPostService.findDiscussPosts(0,0,10);
        List<Map<String, Object>> discussPostList = new ArrayList<>();
        for(DiscussPost post:list){
            Map<String,Object> map = new HashMap<>();
            map.put("post",post);
            User user = userService.findUserById(post.getUserId());
            map.put("user",user);
            discussPostList.add(map);
        }
        for(Map<String,Object> map:discussPostList){
            System.out.println(map.get("post").toString());
            System.out.println(map.get("user").toString());
        }

        Page page = new Page();
        page.setRows(discussPostService.findDiscussPostRows(0));
        System.out.println(page.getFrom());
        System.out.println(page.getTo());

    }


    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("zhaoxue");
        user.setPassword("abc");
        user.setSalt("efg");
        user.setActivationCode("ffafafa");
        user.setEmail("test2@qq.com");
        user.setHeaderUrl("http://images.nowcoder.com/head/138t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);
    }

    //测试登录数据的增删改查
    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket =new LoginTicket();
        loginTicket.setUserId(144);
        loginTicket.setTicket("abjadkjadkjajdajbkjafa");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+3600*12));
        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket2 = loginTicketMapper.selectLoginTicket("abjadkjadkjajdajbkjafa");
        System.out.println(loginTicket2.getUserId());

        loginTicketMapper.updateLoginTicket("abjadkjadkjajdajbkjafa",1);
    }

    @Test
    public void testMessage(){
        List<Message> list = messageMapper.selectConversations(111,0,20);
        for(Message message:list){
            System.out.println(message.toString());
        }

        int count = messageMapper.selectConversationsCount(111);
        System.out.println(count);

        list = messageMapper.selectLetters("111_112",0,20);
        for(Message message:list){
            System.out.println(message.toString());
        }

        count = messageMapper.selectLettersCount("111_112");
        System.out.println(count);

        count = messageMapper.selectLetterUnreadCount(131,"111_131");
        System.out.println(count);

        count = messageMapper.selectLetterUnreadCount(131,null );
        System.out.println(count);


    }




}
