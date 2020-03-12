package com.nowcoder.community.controller;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.ibatis.ognl.ObjectElementsAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;



    @RequestMapping(path="/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content){
        User user = hostHolder.getUser();
        if(user == null){
            return CommunityUtil.getJSONString(403,"你还没有登陆！");
        }

        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);

        return CommunityUtil.getJSONString(0,"发布成功");

    }

    @RequestMapping(path = "/detail/{postId}",method = RequestMethod.GET)
    public String getDiscussPostDetail(@PathVariable("postId")
                                                   int postId, Model model, Page page){
        //查询帖子
        DiscussPost post = discussPostService.findDiscussPostById(postId);
        model.addAttribute("post",post);
        //用户名
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);
        //评论处理
        //分页处理
        page.setLimit(5);
        page.setPath("/discuss/detail/"+postId);
        page.setRows(post.getCommentCount());
        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_TYPE_DISCUSSPOST,postId,page.getOffset(),page.getLimit());
        //我们要显示的不只是comment的信息 还有发表comment的用户的信息，reply的信息等等，利用map封装信息再装入list
        List<Map<String,Object>> commentVoList = new ArrayList<>();
        if(commentList!=null){
            for (Comment comment:commentList){
                Map<String,Object> commentVo = new HashMap<>();
                //封装这条评论的comment信息
                commentVo.put("comment",comment);
                //封装这条评论的用户信息
                commentVo.put("user",userService.findUserById(comment.getUserId()));
                //封装这条评论的reply信息（包括reply信息，发表reply的用户信息，reply的目标用户信息）
                List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT,comment.getId(),0,Integer.MAX_VALUE);
                List<Map<String,Object>> replyVoList = new ArrayList<>();
                if(replyList != null){
                    for (Comment reply : replyList){
                        Map<String,Object> replyVo = new HashMap<>();
                        replyVo.put("reply",reply);
                        replyVo.put("user",userService.findUserById(reply.getUserId()));
                        replyVo.put("target",reply.getTargetId()==0?null:userService.findUserById(reply.getTargetId()));
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys",replyVoList);
                commentVo.put("replyCount",commentService.findCountByEntity(ENTITY_TYPE_COMMENT,comment.getId()));
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments",commentVoList);
        return "/site/discuss-detail";

    }

}
