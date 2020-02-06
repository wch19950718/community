package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {
    @Autowired
    private DiscussPostMapper discussPostMapper;


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

    @Test
    public void testHome(){
        List<DiscussPost> list = discussPostService.findDiscussPosts(0,0,10);
        List<Map<String, Object>> discussPostList = new ArrayList<>();
        for(DiscussPost post:list){
            Map<String,Object> map = new HashMap<>();
            map.put("post",post);
            User user = userService.findUser(post.getUserId());
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

}
