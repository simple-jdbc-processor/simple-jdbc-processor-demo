package com.example.service;

import com.example.Application;
import com.example.domain.Balance;
import com.example.domain.BalanceExample;
import com.example.repository.BalanceRepository;
import com.example.service.BalanceService;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@SpringBootTest(classes = Application.class)
@Slf4j
public class BalanceServiceTest {

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private BalanceRepository balanceRepository;


    @Test
    public void testInsertSelective() {
        Balance balance = new Balance()
                .setId(1L)
                .setBalance(new java.math.BigDecimal("100"))
                .setFrozen(new java.math.BigDecimal("0"))
                .setCreateTime(new java.util.Date())
                .setUpdateTime(new java.util.Date());
        balanceService.insertSelective(balance);
    }

    @Test
    public void testSelectByPrimaryKey() {
        balanceService.selectByPrimaryKey(1L);
    }

    @Test
    public void testIncrement() {
        balanceRepository.increment(1L, new java.math.BigDecimal("100"));
    }

    @Test
    public void testFrozen() {
        balanceRepository.frozen(1L, new java.math.BigDecimal("99"));
    }

    @Test
    public void testBatchIncrement() {
        List<Balance> balances = Arrays.asList(
                new Balance().setId(1L).setBalance(new java.math.BigDecimal("100")),
                new Balance().setId(2L).setBalance(new java.math.BigDecimal("200"))
        );
        balanceRepository.batchIncrement(balances);
    }

    @Test
    public void testUpdateBatchByExampleSelective() {
        BalanceExample update1 = BalanceExample.create()
                .set("balance = balance + ?", new BigDecimal("100"))
                .andIdEqualTo(1L);
        BalanceExample update2 = BalanceExample.create()
                .set("balance = balance + ?", new BigDecimal("100"))
                .and("balance > frozen")
                .andIdEqualTo(2L);
        balanceRepository.updateBatchByExample(Arrays.asList(update1, update2));
    }

    @Test
    public void sumBalance() {
        List<Balance> balances = balanceRepository.sumBalance();
        log.info("sum balance:{}", balances);
    }
}
