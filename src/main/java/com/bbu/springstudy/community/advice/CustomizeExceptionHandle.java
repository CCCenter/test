package com.bbu.springstudy.community.advice;

import com.bbu.springstudy.community.exception.CustomizeException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class CustomizeExceptionHandle {
    //拦截错误页面 拦截mvc可以处理的异常 （不包括请求的异常）
    @ExceptionHandler(Exception.class)
    ModelAndView handle(Throwable e, Model model) {
        if(e instanceof CustomizeException){
            model.addAttribute("message",e.getMessage());
        }else{
            model.addAttribute("message","服务器冒烟了耶！！稍后再试试看。");
        }
        return new ModelAndView("error");
    }
}
