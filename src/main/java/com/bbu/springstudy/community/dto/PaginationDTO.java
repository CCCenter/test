package com.bbu.springstudy.community.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationDTO<T> {
    private List<T> data;
    private boolean showPrevious;//上一页
    private boolean showFirstPage;//第一页
    private boolean showNext;//下一页
    private boolean showEndPage;//最后一页
    private Integer totalPage;

    private Integer page;//当前页面

    private List<Integer> pages = new ArrayList<>();//显示页面的 页码

    public void setPagination(Integer totalCount, Integer page, Integer size) {

        if(totalCount % size == 0){
            totalPage = totalCount / size;
        }else {
            totalPage = (totalCount / size) + 1;
        }

        if (page < 1){
            this.page = 1;
        }else if (page > totalPage){
            this.page = totalPage;
        }else{
            this.page = page;
        }

        pages.add(this.page);

        for (int i = 1; i <= 3; i++){
            if((this.page - i) > 0){
                pages.add(0,this.page - i);
            }
            if((this.page + i) <= totalPage){
                pages.add(this.page + i);
            }
        }

    //是否展示上一页
        if (page <= 1){
            showPrevious = false;
        }else{
            showPrevious = true;
        }
    //是否展示下一页
        if(page >= totalPage){
            showNext = false;
        }else{
            showNext = true;
        }
    //是否展示 回到首页
        if(!pages.contains(1) && totalPage > 0){
            showFirstPage = true;
        }else{
            showFirstPage = false;
        }
        //是否展示 去尾页
        if(pages.contains(totalPage)){
            showEndPage = false;
        }else{
            showEndPage = true;
        }
    }
}
