package com.example.controller;


import com.example.domain.User;
import com.example.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RequestMapping(value = "api/user")
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping("{id}")
    public User getById(@PathVariable Long id) {
        return userService.selectByPrimaryKey(id);
    }

}
