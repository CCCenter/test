package com.bbu.springstudy.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
//Controller 标记后 代表允许这个类接收前端的请求

/**
 *
 */
@Controller
public class HelloController {
    //ctrl + p 提示
    @GetMapping("/hello")
    public String hello(@RequestParam(name = "name") String name, Model model){
        System.out.println(name);
        model.addAttribute("name" , name);
        //当返回的是一个字符串的时候 会自动的在 resources下的templates 找到相应的html页面 进行渲染
        return "hello";
    }
}
