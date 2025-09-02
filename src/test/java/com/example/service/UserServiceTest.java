package com.example.service;

import com.example.Application;
import com.example.domain.User;
import com.example.domain.UserExample;
import com.example.enums.UserStatus;
import com.example.repository.UserRepository;
import com.example.service.UserService;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

@SpringBootTest(classes = Application.class)
@Slf4j
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;


    @Test
    public void testInsertSelective() {
        User user = new User();
        user.setUsername("test");
        user.setPassword(UUID.randomUUID().toString());
        user.setNickname(UUID.randomUUID().toString());
        user.setStatus(UserStatus.NORMAL);
        user.setCreateTime(new java.util.Date());
        user.setUpdateTime(new java.util.Date());
        userRepository.insertSelective(user);
    }

    @Test
    public void testSelectByPrimaryKey() {
        User user = userRepository.selectByPrimaryKey(1L);
        log.info("user: {}", user);
    }

    @Test
    public void testSelectByExample() {
        UserExample query = UserExample.create()
                .andUsernameEqualTo("test");
        User user = userRepository.selectOne(query);
        log.info("user: {}", user);
    }


}
