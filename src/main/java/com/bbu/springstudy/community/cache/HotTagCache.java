package com.bbu.springstudy.community.cache;

import com.bbu.springstudy.community.dto.HotTagDTO;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.*;

// 默认单例模式 交给Spring 管理
@Component
@Data
public class HotTagCache {
    private List<String> hots = new ArrayList<>();

    public void updateTags(Map<String  , Integer> tags){
        int max = 10;
        //优先队列  实现大顶堆小顶堆
        PriorityQueue<HotTagDTO> priorityQueue = new PriorityQueue<>(max);

        tags.forEach((name, priority) -> {
            HotTagDTO hotTagDTO = new HotTagDTO();
            hotTagDTO.setName(name);
            hotTagDTO.setPriority(priority);
            if(priorityQueue.size() < max){
                priorityQueue.add(hotTagDTO);
            }else{
                //PriorityQueue.peek(); 获得最小的元素
                HotTagDTO minHot = priorityQueue.peek();
                if(hotTagDTO.compareTo(minHot) > 0){
                    //PriorityQueue.poll() 删除最小的元素
                    priorityQueue.poll();
                    priorityQueue.add(hotTagDTO);
                }
            }
        });
        hots.clear();
        HotTagDTO poll = priorityQueue.poll();
        hots.add(poll.getName());
        while (poll != null){
            poll = priorityQueue.poll();
            if(poll != null){
                hots.add(0,poll.getName());
            }
        }
        System.out.println(hots);
    }
}
