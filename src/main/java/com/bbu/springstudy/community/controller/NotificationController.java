package com.bbu.springstudy.community.controller;

import com.bbu.springstudy.community.enums.NotificationTypeEnum;
import com.bbu.springstudy.community.model.Notification;
import com.bbu.springstudy.community.model.User;
import com.bbu.springstudy.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notification/{id}")
    public String profile(HttpServletRequest request,
            @PathVariable("id") Long id){
        User user = (User) request.getSession().getAttribute("user");

        if(user == null){
            return "redirect:/";
        }
        Notification notification = notificationService.read(id , user);
        if(Objects.equals(notification.getType(), NotificationTypeEnum.REPLY_QUESTION.getType())){
            return "redirect:/question/"+ notification.getOuterId();
        }else if(Objects.equals(notification.getType(), NotificationTypeEnum.REPLY_COMMENT.getType())){
            return "redirect:/question/"+ notification.getOuterId();
        }else{
            return "redirect:/";
        }

    }
}
