package com.bbu.springstudy.community.exception;

public class CustomizeException extends RuntimeException {
    private String message;
    private Integer code;

    public CustomizeException(CustomizeErrorCodeable errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public Integer getCode() {
        return code;
    }
}
