package com.example.service;

import com.example.Application;
import com.example.domain.Order;
import com.example.domain.OrderExample;
import com.example.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@Transactional  // 添加事务管理，确保测试后数据回滚
@Slf4j
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    // 测试数据
    private static final Long TEST_USER_ID_1 = 1L;
    private static final Long TEST_USER_ID_2 = 2L;
    private static final BigDecimal TEST_AMOUNT_1 = new BigDecimal("100");
    private static final BigDecimal TEST_AMOUNT_2 = new BigDecimal("200");
    private static final Integer BATCH_SIZE = 5;
    private static final Long EXISTING_ORDER_ID = 195416775580001L;

    private AtomicLong idGenerator;
    private List<Long> testOrderIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        idGenerator = new AtomicLong(System.nanoTime());
        testOrderIds.clear();
        
        // 清理测试数据
        OrderExample deleteExample = OrderExample.create()
                .andUserIdIn(Arrays.asList(TEST_USER_ID_1, TEST_USER_ID_2));
        
        Order deleteQuery = new Order().setUserId(TEST_USER_ID_1);
        orderRepository.deleteByExample(deleteQuery, deleteExample);

        deleteQuery.setUserId(TEST_USER_ID_2);
        orderRepository.deleteByExample(deleteQuery, deleteExample);

        log.info("测试数据清理完成");
    }

    @AfterEach
    void tearDown() {
        // 清理测试数据
        if (!testOrderIds.isEmpty()) {
            OrderExample deleteExample = OrderExample.create()
                    .andIdIn(testOrderIds);

            for (Long orderId : testOrderIds) {
                Order deleteQuery = new Order()
                        .setId(orderId)
                        .setUserId(TEST_USER_ID_1);
                orderRepository.deleteByExample(deleteQuery, deleteExample);
                 deleteQuery = new Order()
                        .setId(orderId)
                        .setUserId(TEST_USER_ID_2);
                orderRepository.deleteByExample(deleteQuery, deleteExample);
            }
            testOrderIds.clear();
        }
        log.info("测试数据清理完成");
    }

    // 根据订单ID获取对应的用户ID（模拟分表策略）
    private Long getUserIdForOrderId(Long orderId) {
        // 简单模拟：如果订单ID是我们生成的测试ID，返回相应的用户ID
        for (int i = 0; i < BATCH_SIZE; i++) {
            if (orderId == testOrderIds.get(i)) {
                return TEST_USER_ID_1;
            } else if (orderId == testOrderIds.get(i + BATCH_SIZE)) {
                return TEST_USER_ID_2;
            }
        }
        // 默认返回用户2，因为现有测试数据中使用的是用户2
        return TEST_USER_ID_2;
    }

    @Test
    void testInsert() {
        // 准备测试数据
        Long orderId = idGenerator.incrementAndGet();
        Order order = new Order()
                .setId(orderId)
                .setUserId(TEST_USER_ID_2)
                .setAmount(TEST_AMOUNT_2)
                .setCreateTime(new Date())
                .setUpdateTime(new Date());
        
        // 记录测试ID以便清理
        testOrderIds.add(orderId);
        
        // 执行操作
        orderRepository.insertSelective(order);
        
        // 验证数据是否正确插入
        Order insertedOrder = orderRepository.selectByPrimaryKey(order);
        assertNotNull(insertedOrder, "插入的订单记录应存在");
        assertEquals(orderId, insertedOrder.getId());
        assertEquals(TEST_USER_ID_2, insertedOrder.getUserId());
        assertEquals(TEST_AMOUNT_2.setScale(2, RoundingMode.DOWN), insertedOrder.getAmount().setScale(2, RoundingMode.DOWN));
        
        log.info("testInsert 测试通过");
    }

    @Test
    void testInsertBatch() {
        // 准备测试数据
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < BATCH_SIZE; i++) {
            Long orderId = idGenerator.incrementAndGet();
            Order order = new Order()
                    .setId(orderId)
                    .setUserId(TEST_USER_ID_1)
                    .setAmount(TEST_AMOUNT_1)
                    .setCreateTime(new Date())
                    .setUpdateTime(new Date());
            orders.add(order);
            testOrderIds.add(orderId);
        }
        for (int i = 0; i < BATCH_SIZE; i++) {
            Long orderId = idGenerator.incrementAndGet();
            Order order = new Order()
                    .setId(orderId)
                    .setUserId(TEST_USER_ID_2)
                    .setAmount(TEST_AMOUNT_2)
                    .setCreateTime(new Date())
                    .setUpdateTime(new Date());
            orders.add(order);
            testOrderIds.add(orderId);
        }
        
        // 执行操作
        orderRepository.insertBatch(orders);
        
        // 验证数据是否正确插入
        for (Order order : orders) {
            Order insertedOrder = orderRepository.selectByPrimaryKey(order);
            assertNotNull(insertedOrder, "批量插入的订单记录应存在");
            assertEquals(order.getId(), insertedOrder.getId());
            assertEquals(order.getUserId(), insertedOrder.getUserId());
            assertEquals(order.getAmount().setScale(2, RoundingMode.DOWN), insertedOrder.getAmount().setScale(2, RoundingMode.DOWN));
        }
        
        log.info("testInsertBatch 测试通过");
    }

    @Test
    void testSelectByPrimaryKey() {
        // 首先插入一条测试数据
        Long orderId = idGenerator.incrementAndGet();
        Order order = new Order()
                .setId(orderId)
                .setUserId(TEST_USER_ID_2)
                .setAmount(TEST_AMOUNT_2)
                .setCreateTime(new Date())
                .setUpdateTime(new Date());
        orderRepository.insertSelective(order);
        testOrderIds.add(orderId);
        
        // 执行查询操作
        Order query = new Order()
                .setId(orderId)
                .setUserId(TEST_USER_ID_2); // 分表策略
        Order result = orderRepository.selectByPrimaryKey(query);
        
        // 验证结果
        assertNotNull(result, "查询到的订单记录不应为空");
        assertEquals(orderId, result.getId());
        assertEquals(TEST_USER_ID_2, result.getUserId());
        assertEquals(TEST_AMOUNT_2.setScale(2, RoundingMode.DOWN), result.getAmount().setScale(2, RoundingMode.DOWN));
        
        log.info("testSelectByPrimaryKey 测试通过");
    }

    @Test
    void testUpdateByPrimaryKey() {
        // 首先插入一条测试数据
        Long orderId = idGenerator.incrementAndGet();
        Order order = new Order()
                .setId(orderId)
                .setUserId(TEST_USER_ID_2)
                .setAmount(TEST_AMOUNT_2)
                .setCreateTime(new Date())
                .setUpdateTime(new Date());
        orderRepository.insertSelective(order);
        testOrderIds.add(orderId);
        
        // 准备更新数据
        BigDecimal newAmount = new BigDecimal("300");
        Order updateOrder = new Order()
                .setId(orderId)
                .setUserId(TEST_USER_ID_2) // 分表策略
                .setAmount(newAmount)
                .setUpdateTime(new Date());
        
        // 执行更新操作
        long affectRows = orderRepository.updateByPrimaryKeySelective(updateOrder);
        
        // 验证结果
        assertEquals(1, affectRows, "更新操作应影响1行数据");
        
        // 验证数据是否正确更新
        Order queryOrder = new Order()
                .setId(orderId)
                .setUserId(TEST_USER_ID_2);
        Order updatedOrder = orderRepository.selectByPrimaryKey(queryOrder);
        assertEquals(newAmount.setScale(2, RoundingMode.DOWN), updatedOrder.getAmount().setScale(2, RoundingMode.DOWN), "订单金额应被正确更新");
        
        log.info("testUpdateByPrimaryKey 测试通过");
    }

    @Test
    void testSelectByExample() {
        // 首先插入几条测试数据
        List<Order> testOrders = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Long orderId = idGenerator.incrementAndGet();
            Order order = new Order()
                    .setId(orderId)
                    .setUserId(TEST_USER_ID_2)
                    .setAmount(TEST_AMOUNT_2)
                    .setCreateTime(new Date())
                    .setUpdateTime(new Date());
            orderRepository.insertSelective(order);
            testOrderIds.add(orderId);
            testOrders.add(order);
        }
        
        // 准备查询条件
        Order query = new Order()
                .setUserId(TEST_USER_ID_2); // 分表策略
        OrderExample orderExample = new OrderExample()
                .andUserIdEqualTo(TEST_USER_ID_2)
                .limit(10);
        
        // 执行查询操作
        List<Order> results = orderRepository.selectByExample(query, orderExample);
        
        // 验证结果
        assertNotNull(results, "查询结果不应为空");
        assertFalse(results.isEmpty(), "查询应返回至少一条记录");
        
        log.info("testSelectByExample 测试通过");
    }

    @Test
    void testDeleteByPrimaryKey() {
        // 首先插入一条测试数据
        Long orderId = idGenerator.incrementAndGet();
        Order order = new Order()
                .setId(orderId)
                .setUserId(TEST_USER_ID_2)
                .setAmount(TEST_AMOUNT_2)
                .setCreateTime(new Date())
                .setUpdateTime(new Date());
        orderRepository.insertSelective(order);
        
        // 执行删除操作
        Order deleteQuery = new Order()
                .setId(orderId)
                .setUserId(TEST_USER_ID_2); // 分表策略
        long affectRows = orderRepository.deleteByPrimaryKey(deleteQuery);
        
        // 验证结果
        assertEquals(1, affectRows, "删除操作应影响1行数据");
        
        // 验证数据是否已删除
        Order queryOrder = new Order()
                .setId(orderId)
                .setUserId(TEST_USER_ID_2);
        Order deletedOrder = orderRepository.selectByPrimaryKey(queryOrder);
        assertNull(deletedOrder, "删除的订单记录不应存在");
        
        log.info("testDeleteByPrimaryKey 测试通过");
    }

    @Test
    void testSelectNonExistentOrder() {
        // 尝试查询不存在的订单
        Long nonExistentId = -999L;
        Order query = new Order()
                .setId(nonExistentId)
                .setUserId(TEST_USER_ID_2); // 分表策略
        Order result = orderRepository.selectByPrimaryKey(query);
        
        // 验证结果
        assertNull(result, "查询不存在的订单应返回null");
        
        log.info("testSelectNonExistentOrder 测试通过");
    }

    @Test
    void testDeleteByExample() {
        // 首先插入几条测试数据
        List<Order> testOrders = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Long orderId = idGenerator.incrementAndGet();
            Order order = new Order()
                    .setId(orderId)
                    .setUserId(TEST_USER_ID_1)
                    .setAmount(TEST_AMOUNT_1)
                    .setCreateTime(new Date())
                    .setUpdateTime(new Date());
            orderRepository.insertSelective(order);
            testOrders.add(order);
        }
        
        // 准备删除条件
        Order deleteQuery = new Order()
                .setUserId(TEST_USER_ID_1); // 分表策略
        OrderExample deleteExample = new OrderExample()
                .andUserIdEqualTo(TEST_USER_ID_1);
        
        // 执行删除操作
        long affectRows = orderRepository.deleteByExample(deleteQuery, deleteExample);
        
        // 验证结果
        assertEquals(3, affectRows, "删除操作应影响3行数据");
        
        // 验证数据是否已删除
        Order query = new Order()
                .setUserId(TEST_USER_ID_1);
        OrderExample queryExample = new OrderExample()
                .andUserIdEqualTo(TEST_USER_ID_1);
        List<Order> remainingOrders = orderRepository.selectByExample(query, queryExample);
        assertTrue(remainingOrders.isEmpty(), "符合条件的订单应全部删除");
        
        log.info("testDeleteByExample 测试通过");
    }
}