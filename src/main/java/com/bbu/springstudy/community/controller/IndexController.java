package com.bbu.springstudy.community.controller;

import com.bbu.springstudy.community.dto.QuestionDTO;
import com.bbu.springstudy.community.mapper.UserMapper;
import com.bbu.springstudy.community.model.User;
import com.bbu.springstudy.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionService questionService;

    @GetMapping("/")
    //什么都不输入默认返回index
    public String index(HttpServletRequest request,
                        Model model
    ){
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie : cookies) {
                if("token".equals(cookie.getName())){
                    String token = cookie.getValue();
                    User user = userMapper.findByToken(token);
                    if(user != null){
                        request.getSession().setAttribute("user",user);
                    }
                    break;
                }
            }
        }

        List<QuestionDTO> questionList = questionService.list();
        model.addAttribute("questions",questionList);
        return "index";
    }
}
