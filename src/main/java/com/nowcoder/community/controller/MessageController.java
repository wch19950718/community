package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

    @RequestMapping(path = "/letter/list",method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){
        //获取当前用户
        User user = hostHolder.getUser();
        //分页处理
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationsCount(user.getId()));
        //获取每页的会话列表（返回最新的消息）
        List<Message> conversationList = messageService.findConversations(user.getId(),page.getOffset(),page.getLimit());
        //除了返回消息外，我们还需要一些其他信息，该会话的未读消息数量，该会话的消息数量-->用map封装
        List<Map<String,Object>> conversations = new ArrayList<>();
        if(conversationList != null){
            for(Message message:conversationList){
                Map<String,Object> map = new HashMap<>();
                map.put("conversation",message);
                map.put("unreadCount",messageService.findLetterUnreadCount(user.getId(),message.getConversationId()));
                map.put("letterCount",messageService.findLettersCount(message.getConversationId()));
                int targetId = message.getFromId()==user.getId()?message.getToId():message.getFromId();
                User target = userService.findUserById(targetId);
                map.put("target",target);

                conversations.add(map);
            }

        }
        model.addAttribute("conversations",conversations);

        //查询所有未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(),null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);

        return "/site/letter";
    }

    @RequestMapping(path = "/letter/detail/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId,Page page,Model model){
        //分页设置
        page.setLimit(5);
        page.setPath("/letter/detail/"+conversationId);
        page.setRows(messageService.findLettersCount(conversationId));
        //某页消息列表
        List<Message> letterList = messageService.findLetters(conversationId,page.getOffset(),page.getLimit());
        //封装from的用户信息
        List<Map<String,Object>> letters = new ArrayList<>();
        if(letterList!= null){
            for(Message message:letterList){
                Map<String,Object> map = new HashMap<>();
                map.put("letter",message);
                map.put("fromUser",userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters",letters);

        //需要target的用户信息，封装了一个找target的方法
        User target = getTarget(conversationId);
        model.addAttribute("target",target);

        //将该会话中未读状态的消息状态改为已读
        List<Integer> unreadIds = getUnreadLetterIds(letterList);
        if (!unreadIds.isEmpty()){
            messageService.readLetter(unreadIds);
        }
        return "/site/letter-detail";
    }

    //寻找未读状态的消息的id列表,并且toUser是当前用户
    private List<Integer> getUnreadLetterIds(List<Message> letterList){
        List<Integer> unreadIds = new ArrayList<>();
        if(letterList != null){
            for (Message message:letterList){
                if (message.getToId()==hostHolder.getUser().getId() && message.getStatus()==0){
                    unreadIds.add(message.getId());
                }
            }
        }
        return unreadIds;
    }


    //寻找目标用户的方法
    private User getTarget(String conversationId){
        String[] ids = conversationId.split("_");

        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if(hostHolder.getUser().getId()==id0){
            return userService.findUserById(id1);
        }else{
            return userService.findUserById(id0);
        }
    }


    @RequestMapping(path = "/letter/send",method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content){
        User toUser = userService.findUserByName(toName);
        if(toUser == null){
           return CommunityUtil.getJSONString(1,"目标用户不存在");
        }else {
            //目标用户存在
            Message message = new Message();
            message.setFromId(hostHolder.getUser().getId());
            message.setToId(toUser.getId());
            if(toUser.getId() < hostHolder.getUser().getId()){
                message.setConversationId(toUser.getId()+"_"+hostHolder.getUser().getId());
            }else {
                message.setConversationId(hostHolder.getUser().getId()+"_"+toUser.getId());
            }
            message.setContent(content);
            message.setCreateTime(new Date());
            messageService.addLetter(message);

            return CommunityUtil.getJSONString(0);
        }
    }


    @RequestMapping(path = "/letter/delete",method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(int id){
        messageService.deleteLetter(id);
        return CommunityUtil.getJSONString(0);

    }


}
