package com.bbu.springstudy.community.exception;

public enum CustomizeErrorCode implements CustomizeErrorCodeable {
    QUESTION_NOT_FOUND(2001, "你找的问题不在的，要不换一个试试？"),
    TARGET_PARAM_NOT_FOUND(2002, "为选中任何问题或评论进行回复。"),
    NO_LOGIN(2003, "当前操作需要登陆，请先登陆。"),
    SYS_ERROR(2004,"服务器冒烟了耶！！稍后再试试看。"),
    TYPE_PARAM_WRONG(2005,"评论类型错误或不存在。"),
    COMMENT_NOT_FOUND(2006,"你回复的评论不存在了。"),
    COMMENT_IS_EMPTY(2007,"输入内容不能为空。"),
    READ_NOTIFICATION_FAIL(2008,"读取通知失败。"),
    NOTIFICATION_NOT_FOUND(2009,"通知不存在。"),
    ;

    private String message;
    private Integer code;

    CustomizeErrorCode(Integer code, String message) {
        this.message = message;
        this.code = code;
    }

    @Override
    public String getMessage(){
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}
