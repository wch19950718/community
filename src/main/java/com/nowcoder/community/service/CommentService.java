package com.nowcoder.community.service;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService implements CommunityConstant {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private DiscussPostService discussPostService;

    public List<Comment> findCommentsByEntity(int entityType,int entityId,int offset,int limit){
        return commentMapper.selectCommnetsByEntity(entityType,entityId,offset,limit);
    }

    public int findCountByEntity(int entityType,int entityId){
        return commentMapper.selectCountByEntity(entityType,entityId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment){
        System.out.println(comment.getEntityType());
        System.out.println(comment.getEntityId());
        System.out.println(comment.getContent());
        //判空
        if(comment==null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        //对传入的comment的HTML标签和内容进行过滤
        //标签转义（例如“<”,保存到数据库时会变成“&lt;”,但是你想保存到数据库的就是“<”,因此你可以用HtmlUtils.htmlUnescape()进行转义一下，再保存到数据库就ok了）
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        //过滤敏感词
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows = commentMapper.addComment(comment);

        //对discuss_post表中的comment_count更新
        //得是帖子的评论才可以更新，评论的回复不能
        if(comment.getEntityType() == ENTITY_TYPE_DISCUSSPOST){
            int count = commentMapper.selectCountByEntity(comment.getEntityType(),comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(),count);
        }
        return rows;
    }
}
