package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    public static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    //固定的敏感词替换字符串
    public static final String REPLACEMENT = "***";
    //根节点
    private TrieNode rootNode = new TrieNode();



    /**
     * 编写敏感词过滤方法
     * @param text 待过滤文本
     * @return 返回已过滤文本
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        TrieNode tempNode = rootNode;
        int begin = 0;
        int position = 0;
        StringBuilder sb = new StringBuilder();
        while(begin<text.length()){
            char c = text.charAt(position);
            //跳过符号,符号在判断中间的忽略，在前面的记下
            if(this.isSymbol(c)){
                if(tempNode==rootNode){
                    //符号出现时还在root说明在判断的前面，记下，begin和position都向下一个
                    sb.append(c);
                    begin++;
                }
                //如果不在root，说明符号在判断敏感词的中间，position直接向下一个，begin不变
                position++;
                continue;
            }

            //如果没遇到符号,检查子节点和position位置上字符是否一致，会有三个情况
            tempNode = tempNode.getSubNode(c);
            if(tempNode==null){
                //不一致，那么说明begin开头的字符串不是敏感词，begin位置上的字符记录，begin向下，position回到begin一起
                sb.append(text.charAt(begin));
                position = ++begin;
                tempNode = rootNode;
            }else if(tempNode.isKeywordEnd == true){
                //一致，并且还是最后一个
                sb.append(REPLACEMENT);
                begin = ++position;
                tempNode = rootNode;
            }else{
                if(position<text.length()-1){

                    position++;
                }else{
                    sb.append(text.charAt(begin));
                    position = ++begin;
                    tempNode = rootNode;
                }
            }

        }
        return sb.toString();


    }
    //判断是否为特殊符号
    private boolean isSymbol(Character c){
        //0x2E80~0x9FFF是东亚文字，我们不认为他是特殊符号
        //CharUtils.isAsciiAlphanumeric(c)是符号就返回false
        return !CharUtils.isAsciiAlphanumeric(c) && (c<0x2E80 || c>0x9FFF);
    }


    //根据敏感词，初始化前缀树
    //希望在程序启动后就立刻初始化，我们选用postConstruct在Bean初始化（程序启动是就会初始化Bean）之后初始化前缀树
    @PostConstruct
    private void init(){
        //读取敏感词

        try(
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                ) {
            String keyword;
            //如果我读到了敏感词 就向开始向树中添加
            while((keyword = reader.readLine())!=null){
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("敏感词文件读取失败："+e.getMessage());
        }

    }

    private void addKeyword(String keyword){
        TrieNode tempNode = rootNode;
        for(int i=0;i<keyword.length();i++){
            char c = keyword.charAt(i);
            //看c是否已经存在于子节点上了
            TrieNode subNode = tempNode.getSubNode(c);
            //如果没有就初始化一个节点,并将该节点挂到当前节点（指针节点）下面
            if(subNode==null){
                subNode = new TrieNode();
                tempNode.addTrieNode(c,subNode);
            }
            //如果已经存在，或者不存在但是初始化好了一个新的子节点了，指针节点向下移动
            tempNode = subNode;
            //如果遍历到最后一个字符了，标记出来
            if(i==keyword.length()-1){
                tempNode.isKeywordEnd = true;
            }
        }
    }


    //定义前缀树结构，内部类
    private class TrieNode{
        //是否是结尾节点(敏感词的结束)
        private boolean isKeywordEnd = false;
        //定义子节点
        private Map<Character,TrieNode> subNode = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        //子节点的get，set方法
        public TrieNode getSubNode(Character c){
            return subNode.get(c);

        }
        public void addTrieNode(Character c,TrieNode node){
            subNode.put(c,node);
        }


    }

}
