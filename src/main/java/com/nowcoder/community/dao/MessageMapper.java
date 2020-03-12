package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    //查询某页的会话（每个会话只返回最新的消息）
    List<Message> selectConversations(int userId,int offset,int limit);
    //查询所有的会话数量
    int selectConversationsCount(int userId);
    //查询某个会话中某页的消息
    List<Message> selectLetters(String conversationId,int offset,int limit);
    //查询某个会话中消息的数量
    int selectLettersCount(String conversationId);
    //查询未读消息数量（所有的和某个会话的,在数据库操作中动态判断即可实现两个查询）
    int selectLetterUnreadCount(int userId,String conversationId);
    //添加消息
    int insertMessage(Message message);
    //更新消息状态（修改为已读或者是删除）,点进去是这个会话的所有未读消息都会修改。
    int updateStatus(List<Integer> ids,int status);
    //更新消息状态（修改为删除）一次只会修改一个为删除。
    int updateStatusToDelete(int id ,int status);
}
