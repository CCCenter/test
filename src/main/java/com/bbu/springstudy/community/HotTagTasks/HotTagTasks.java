package com.bbu.springstudy.community.HotTagTasks;


import com.bbu.springstudy.community.cache.HotTagCache;
import com.bbu.springstudy.community.mapper.QuestionMapper;
import com.bbu.springstudy.community.model.Question;
import com.bbu.springstudy.community.model.QuestionExample;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class HotTagTasks {
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private HotTagCache hotTagCache;

    @Scheduled(fixedDelay = 20000)
//    @Scheduled(cron = "0 0 1 * * *")
    public void hotTagSchedule(){
        int offset = 0;
        int limit = 5;

        List<Question> list = new ArrayList<>();
        Map<String, Integer> priorities = new HashMap<>();

        while(offset == 0 || list.size() == limit){
            list = questionMapper.selectByExampleWithRowbounds(new QuestionExample(), new RowBounds(offset, limit));
            if(list.size() == 0) break;
            for (Question question : list) {
                String[] tags = StringUtils.split(question.getTag(), ",");
                for (String tag : tags) {
                    Integer priority = priorities.get(tag);
                    if(priority == null){
                        priorities.put(tag, 5 + question.getCommentCount());
                    }else{
                        priorities.put(tag, priority + 5 + question.getCommentCount());
                    }
                }
            }
            offset += limit;
        }

        priorities.forEach(
                (k, v) -> {
                    System.out.print(k);
                    System.out.print(":");
                    System.out.print(v);
                    System.out.println();
                }
        );
        hotTagCache.updateTags(priorities);
    }
}
