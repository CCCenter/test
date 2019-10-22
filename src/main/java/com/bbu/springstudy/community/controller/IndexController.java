package com.bbu.springstudy.community.controller;

import com.bbu.springstudy.community.dto.PaginationDTO;
import com.bbu.springstudy.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/")
    //什么都不输入默认返回index
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "5") Integer size,
                        @RequestParam(name = "search", required = false ) String search
    ) {

        PaginationDTO pagination = questionService.list(search,page, size);
        model.addAttribute("pagination", pagination);
        return "index";
    }
}
