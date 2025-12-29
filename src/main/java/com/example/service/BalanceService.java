package com.example.service;


import com.example.domain.Balance;
import com.example.domain.BalanceExample;
import com.example.repository.BalanceRepository;
import io.github.simple.jdbc.processor.BaseService;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BalanceService extends BaseService<Balance, Long, BalanceExample> {

    private final BalanceRepository balanceRepository;

    public BalanceService(BalanceRepository balanceRepository){
        super(balanceRepository);
        this.balanceRepository = balanceRepository;
    }

}
