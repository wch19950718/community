package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

@Service
//@Scope("prototype")
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private DiscussPostMapper discussPostMapper;

//    public AlphaService(){
//        System.out.println("AlphaService构造方法");
//    }
//
//    @PostConstruct
//    public void init(){
//        System.out.println("初始化AlphaService");
//    }
//
//    @PreDestroy
//    public void destroy(){
//        System.out.println("销毁AlphaService");
//    }

    public String find(){
        return alphaDao.select();
    }

    /**
     * 这个事务包括一个注册用户和一个发布新人贴两个数据库操作和一个人造错误（不出错的话是看不出来事务的性质的，因为都会成功执行，
     * 但是如果出错了，事务是不会注册用户和发布帖子的，而非事务就会顺序执行）
     */
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public Object save1(){
        //新建用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5("123456"+user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl("http://images.nowcoder.com/head/999t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //发布新人帖子
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle("新人报到");
        discussPost.setContent("hello!我是新人！");
        discussPost.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(discussPost);

        //人造错误
        Integer.valueOf("abc");

        return "ok";

    }

    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                //新建用户
                User user = new User();
                user.setUsername("beta");
                user.setSalt(CommunityUtil.generateUUID().substring(0,5));
                user.setPassword(CommunityUtil.md5("123456"+user.getSalt()));
                user.setEmail("beta@qq.com");
                user.setActivationCode(CommunityUtil.generateUUID());
                user.setHeaderUrl("http://images.nowcoder.com/head/999t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                //发布新人帖子
                DiscussPost discussPost = new DiscussPost();
                discussPost.setUserId(user.getId());
                discussPost.setTitle("新人来报到啦！");
                discussPost.setContent("大家好!我是新人！");
                discussPost.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(discussPost);

                //人造错误
                Integer.valueOf("abc");

                return "ok";
            }
        });
    }



}
