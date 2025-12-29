package com.example.service;


import com.example.domain.User;
import com.example.domain.UserExample;
import com.example.repository.UserRepository;
import io.github.simple.jdbc.processor.BaseService;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService extends BaseService<User, Long, UserExample> {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        super(userRepository);
        this.userRepository = userRepository;
    }

}
