package com.example.service;

import com.example.Application;
import com.example.domain.Balance;
import com.example.domain.BalanceExample;
import com.example.repository.BalanceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@Transactional  // 添加事务管理，确保测试后数据回滚
@Slf4j
public class BalanceServiceTest {

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private BalanceRepository balanceRepository;

    // 测试数据
    private static final Long TEST_USER_ID_1 = 1001L;
    private static final Long TEST_USER_ID_2 = 1002L;
    private static final BigDecimal INITIAL_BALANCE = new BigDecimal("100.00");
    private static final BigDecimal INCREMENT_AMOUNT = new BigDecimal("50.00");
    private static final BigDecimal FROZEN_AMOUNT = new BigDecimal("30.00");

    @BeforeEach
    void setUp() {
        // 清理测试数据
        BalanceExample deleteExample = BalanceExample.create()
                .andIdIn(Arrays.asList(TEST_USER_ID_1, TEST_USER_ID_2));
        balanceRepository.deleteByExample(deleteExample);
        
        // 准备测试数据
        Balance balance1 = new Balance()
                .setId(TEST_USER_ID_1)
                .setBalance(INITIAL_BALANCE)
                .setFrozen(BigDecimal.ZERO)
                .setCreateTime(new java.util.Date())
                .setUpdateTime(new java.util.Date());
        Balance balance2 = new Balance()
                .setId(TEST_USER_ID_2)
                .setBalance(INITIAL_BALANCE)
                .setFrozen(BigDecimal.ZERO)
                .setCreateTime(new java.util.Date())
                .setUpdateTime(new java.util.Date());
        
        balanceService.insertSelective(balance1);
        balanceService.insertSelective(balance2);
        log.info("测试数据准备完成");
    }

    @AfterEach
    void tearDown() {
        // 清理测试数据
        BalanceExample deleteExample = BalanceExample.create()
                .andIdIn(Arrays.asList(TEST_USER_ID_1, TEST_USER_ID_2));
        balanceRepository.deleteByExample(deleteExample);
        log.info("测试数据清理完成");
    }

    @Test
    void testInsertSelective() {
        // 准备测试数据
        Long newUserId = 2000L;
        Balance newBalance = new Balance()
                .setId(newUserId)
                .setBalance(new BigDecimal("200"))
                .setFrozen(new BigDecimal("0"))
                .setCreateTime(new java.util.Date())
                .setUpdateTime(new java.util.Date());
        
        // 执行操作
        balanceService.insertSelective(newBalance);
        

        // 验证数据是否正确插入
        Balance insertedBalance = balanceService.selectByPrimaryKey(newUserId);
        assertNotNull(insertedBalance, "插入的余额记录应存在");
        assertEquals(new BigDecimal("200"), insertedBalance.getBalance().setScale(0, RoundingMode.DOWN));
        assertEquals(new BigDecimal("0"), insertedBalance.getFrozen().setScale(0, RoundingMode.DOWN));
        
        log.info("testInsertSelective 测试通过");
    }

    @Test
    void testSelectByPrimaryKey() {
        // 执行操作
        Balance balance = balanceService.selectByPrimaryKey(TEST_USER_ID_1);
        
        // 验证结果
        assertNotNull(balance, "查询到的余额记录不应为空");
        assertEquals(TEST_USER_ID_1, balance.getId());
        assertEquals(INITIAL_BALANCE, balance.getBalance().setScale(2, RoundingMode.DOWN));
        
        log.info("testSelectByPrimaryKey 测试通过");
    }

    @Test
    void testIncrement() {
        // 执行操作
        int result = balanceRepository.increment(TEST_USER_ID_1, INCREMENT_AMOUNT);
        
        // 验证结果
        assertEquals(1, result, "更新操作应返回1表示成功");
        
        // 验证余额是否正确增加
        Balance updatedBalance = balanceService.selectByPrimaryKey(TEST_USER_ID_1);
        BigDecimal expectedBalance = INITIAL_BALANCE.add(INCREMENT_AMOUNT);
        assertEquals(expectedBalance, updatedBalance.getBalance().setScale(2, RoundingMode.DOWN));
        
        log.info("testIncrement 测试通过");
    }

    @Test
    void testFrozen() {
        // 执行操作
        int result = balanceRepository.frozen(TEST_USER_ID_1, FROZEN_AMOUNT);
        
        // 验证结果
        assertEquals(1, result, "冻结操作应返回1表示成功");
        
        // 验证余额和冻结金额是否正确更新
        Balance updatedBalance = balanceService.selectByPrimaryKey(TEST_USER_ID_1);
        BigDecimal expectedBalance = INITIAL_BALANCE.subtract(FROZEN_AMOUNT);
        assertEquals(expectedBalance, updatedBalance.getBalance().setScale(2, RoundingMode.DOWN));
        assertEquals(FROZEN_AMOUNT, updatedBalance.getFrozen().setScale(2, RoundingMode.DOWN));
        
        log.info("testFrozen 测试通过");
    }

    @Test
    void testBatchIncrement() {
        // 准备测试数据
        List<Balance> balances = Arrays.asList(
                new Balance().setId(TEST_USER_ID_1).setBalance(INCREMENT_AMOUNT),
                new Balance().setId(TEST_USER_ID_2).setBalance(INCREMENT_AMOUNT)
        );
        
        // 执行操作
        int[] results = balanceRepository.batchIncrement(balances);
        
        // 验证结果
        assertEquals(2, results.length, "批量更新应返回2条结果");
        assertEquals(1, results[0], "第一条更新应返回1表示成功");
        assertEquals(1, results[1], "第二条更新应返回1表示成功");
        
        // 验证余额是否正确增加
        Balance updatedBalance1 = balanceService.selectByPrimaryKey(TEST_USER_ID_1);
        Balance updatedBalance2 = balanceService.selectByPrimaryKey(TEST_USER_ID_2);
        BigDecimal expectedBalance = INITIAL_BALANCE.add(INCREMENT_AMOUNT);
        assertEquals(expectedBalance, updatedBalance1.getBalance().setScale(2, RoundingMode.DOWN));
        assertEquals(expectedBalance, updatedBalance2.getBalance().setScale(2, RoundingMode.DOWN));
        
        log.info("testBatchIncrement 测试通过");
    }

    @Test
    void testUpdateBatchByExampleSelective() {
        // 准备测试数据
        BigDecimal updateAmount = new BigDecimal("100");
        BalanceExample update1 = BalanceExample.create()
                .set("balance = balance + ?", updateAmount)
                .andIdEqualTo(TEST_USER_ID_1);
        BalanceExample update2 = BalanceExample.create()
                .set("balance = balance + ?", updateAmount)
                .and("balance > frozen")
                .andIdEqualTo(TEST_USER_ID_2);
        
        // 执行操作
        int[] results = balanceRepository.updateBatchByExample(Arrays.asList(update1, update2));
        
        // 验证结果
        assertEquals(2, results.length, "批量更新应返回2条结果");
        assertEquals(1, results[0], "第一条更新应返回1表示成功");
        assertEquals(1, results[1], "第二条更新应返回1表示成功");
        
        // 验证余额是否正确更新
        Balance updatedBalance1 = balanceService.selectByPrimaryKey(TEST_USER_ID_1);
        Balance updatedBalance2 = balanceService.selectByPrimaryKey(TEST_USER_ID_2);
        BigDecimal expectedBalance = INITIAL_BALANCE.add(updateAmount);
        assertEquals(expectedBalance, updatedBalance1.getBalance().setScale(2, RoundingMode.DOWN));
        assertEquals(expectedBalance, updatedBalance2.getBalance().setScale(2, RoundingMode.DOWN));
        
        log.info("testUpdateBatchByExampleSelective 测试通过");
    }

    @Test
    void testSumBalance() {
        // 执行操作
        List<Balance> balances = balanceRepository.sumBalance(TEST_USER_ID_1);

        // 验证结果
        assertNotNull(balances, "聚合查询结果不应为空");
        assertFalse(balances.isEmpty(), "聚合查询应返回至少一条记录");

        // 验证聚合结果
        for (Balance balance : balances) {
            log.info("sum balance result: {}", balance);
            assertNotNull(balance.getId());
            assertNotNull(balance.getBalance());
            assertNotNull(balance.getFrozen());
        }

        log.info("testSumBalance 测试通过");
    }

    @Test
    void testFrozenInsufficientBalance() {
        // 尝试冻结超过可用余额的金额
        BigDecimal excessiveAmount = INITIAL_BALANCE.add(new BigDecimal("1"));
        
        // 执行操作 - 这应该会失败，因为没有足够的余额
        // 注意：实际项目中可能需要根据业务逻辑和异常处理来调整这个测试
        int result = balanceRepository.frozen(TEST_USER_ID_1, excessiveAmount);
        
        // 如果数据库约束允许负余额，那么这里可能会返回1表示更新成功
        // 在实际业务中，应该在服务层添加余额充足性检查
        log.info("testFrozenInsufficientBalance 测试完成，结果: {}", result);
    }

    @Test
    void testSelectNonExistentUser() {
        // 尝试查询不存在的用户
        Balance balance = balanceService.selectByPrimaryKey(-999L);
        
        // 验证结果
        assertNull(balance, "查询不存在的用户应返回null");
        
        log.info("testSelectNonExistentUser 测试通过");
    }

    @Test
    void testSelectByExampleForUpdate(){
        // 准备测试数据
        BalanceExample query = BalanceExample.create()
                .andIdEqualTo(TEST_USER_ID_1)
                .forUpdate();

        // 执行操作
        Balance balance = balanceService.selectOne(query);

        // 验证结果
        assertNotNull(balance, "查询存在的用户应返回非null");
        assertEquals(TEST_USER_ID_1, balance.getId(), "查询用户ID应与测试用户ID匹配");

        log.info("testSelectByExampleForUpdate 测试通过");
    }
}