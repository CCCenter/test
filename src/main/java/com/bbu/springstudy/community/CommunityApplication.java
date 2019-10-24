package com.bbu.springstudy.community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//启动项目的类 内置tomcat
@SpringBootApplication
//MapperScan 扫描映射
@MapperScan("com.bbu.springstudy.community.mapper")
//启动任务
@EnableScheduling
public class CommunityApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

}
