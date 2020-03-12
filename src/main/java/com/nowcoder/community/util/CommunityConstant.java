package com.nowcoder.community.util;

public interface CommunityConstant {
    //激活成功
    int ACTIVATION_SUCCESS=0;
    //重复激活
    int ACTIVATION_REPEAT=1;
    //激活失败
    int ACTIVATION_FAILURE=2;


    /*
     *默认超时时间
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;
    /*
    * 记住超时时间*/
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;

    /**
     * 给帖子的评论
     */
    int ENTITY_TYPE_DISCUSSPOST = 1;
    /**
     * 给评论的评论（回复）
     */
    int ENTITY_TYPE_COMMENT = 2;

}
