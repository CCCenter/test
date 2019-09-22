package com.bbu.springstudy.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    @GetMapping("/")
    //什么都不输入默认返回index
    public String index(){ return "index";}
}
