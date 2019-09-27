package com.bbu.springstudy.community.controller;

import com.bbu.springstudy.community.dto.AccessTokenDTO;
import com.bbu.springstudy.community.dto.GithubUser;
import com.bbu.springstudy.community.mapper.UserMapper;
import com.bbu.springstudy.community.model.User;
import com.bbu.springstudy.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class AuthorizeController {

    @Autowired//
    private GithubProvider githubProvider;

    @Value("${github.client.id}") //去配置文件读取 当spring容器启动的时候 读取配置文件 把key - value 存到一个map 里面  使用Value注解获取
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletResponse response){//spring 自动把上下文的request 放入
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();

        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setState(state);
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(accessToken);
        if(githubUser != null && githubUser.getId() != null){
            //登陆成功写 cookie和session
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            user.setAvatarUrl(githubUser.getAvatarUrl());

            userMapper.insert(user);

            response.addCookie(new Cookie("token",token));
            //request.getSession().setAttribute("user", user);
            return "redirect:/"; //重定向

        }else{
            //登陆失败 重新登陆
            return "redirect:/"; //重定向
        }
    }
}
