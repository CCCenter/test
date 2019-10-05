package com.bbu.springstudy.community.exception;

public enum CustomizeErrorCode implements CustomizeErrorCodeable {
    QUESTION_NOT_FOUND("你找的问题不在的，要不换一个试试？");

    private String message;

    CustomizeErrorCode(String message) {
        this.message = message;
    }

    @Override
    public String getMessage(){
        return message;
    }
}
