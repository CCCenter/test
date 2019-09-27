package com.bbu.springstudy.community.dto;
//dto ==> 数据传输模型

import lombok.Data;

@Data
public class AccessTokenDTO {
    private String client_id;
    private String client_secret;
    private String code;
    private String redirect_uri;
    private String state;
}
