package com.nowcoder.community.dao;


import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);
//    @Param用来给参数取别名，单参数的情况下必须取别名才能让动态mysql识别
    int selectDiscussPostRows(@Param("userId") int userId);
 }
