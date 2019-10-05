package com.bbu.springstudy.community.service;

import com.bbu.springstudy.community.mapper.UserMapper;
import com.bbu.springstudy.community.model.User;
import com.bbu.springstudy.community.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public void createOrUpdate(User user) {
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andAccountIdEqualTo(user.getAccountId());
        List<User> users = userMapper.selectByExample(userExample);
        if(users.size() == 0){
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        }else{
            User dbUser = users.get(0);

            User updateUser = new User();
            updateUser.setToken(user.getToken());
            updateUser.setName(user.getName());
            updateUser.setGmtModified(System.currentTimeMillis());
            updateUser.setAvatarUrl(user.getAvatarUrl());
            UserExample userExample1 = new UserExample();
            userExample1.createCriteria().andIdEqualTo(dbUser.getId());
            userMapper.updateByExampleSelective(updateUser, userExample1);
        }
    }
}
