package com.nowcoder.community;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.entity.Comment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CommentTest {

    @Autowired
    private CommentMapper commentMapper;

    @Test
    public void testComment(){
        int count = commentMapper.selectCountByEntity(1,280);
        System.out.println(count);


        List<Comment> comments = commentMapper.selectCommnetsByEntity(1,280,0,20);
        for(Comment comment:comments){
            System.out.println(comment.toString());
        }
    }

}
