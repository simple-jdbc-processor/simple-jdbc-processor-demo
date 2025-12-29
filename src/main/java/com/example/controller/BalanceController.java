package com.example.controller;


import com.example.domain.Balance;
import com.example.service.BalanceService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RequestMapping(value = "api/balance")
@RestController
public class BalanceController {

    private final BalanceService balanceService;


}
