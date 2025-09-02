package com.example.service;

import com.example.Application;
import com.example.domain.Order;
import com.example.domain.OrderExample;
import com.example.repository.OrderRepository;
import com.example.service.OrderService;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootTest(classes = Application.class)
@Slf4j
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    AtomicLong idGenerator = new AtomicLong(System.nanoTime());


    @Test
    public void testInsertBatch() {
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Order order = new Order()
                    .setId(idGenerator.incrementAndGet())
                    .setUserId(1L)
                    .setAmount(new java.math.BigDecimal("100"))
                    .setCreateTime(new java.util.Date())
                    .setUpdateTime(new java.util.Date());
            orders.add(order);
        }
        for (int i = 0; i < 100; i++) {
            Order order = new Order()
                    .setId(idGenerator.incrementAndGet())
                    .setUserId(2L)
                    .setAmount(new java.math.BigDecimal("200"))
                    .setCreateTime(new java.util.Date())
                    .setUpdateTime(new java.util.Date());
            orders.add(order);
        }
        orderRepository.insertBatch(orders);
    }

    @Test
    public void testInsert() {
        Order order = new Order()
                .setId(idGenerator.incrementAndGet())
                .setUserId(2L)
                .setAmount(new java.math.BigDecimal("200"))
                .setCreateTime(new java.util.Date())
                .setUpdateTime(new java.util.Date());
        orderRepository.insertSelective(order);
    }

    @Test
    public void testSelectByPrimaryKey() {
        Order query = new Order()
                .setId(195416775580001L)
                .setUserId(2L);// 分表策略
        Order order1 = orderRepository.selectByPrimaryKey(query);
        log.info("order1={}", order1);
    }

    @Test
    public void testUpdateByPrimaryKey() {
        Order query = new Order()
                .setId(195416775580001L)
                .setUpdateTime(new Date())
                .setUserId(2L);// 分表策略
        long affect = orderRepository.updateByPrimaryKeySelective(query);
        log.info("order1={}", affect);
    }

    @Test
    public void testSelectByExample() {
        Order query = new Order()
                .setUpdateTime(new Date())
                .setUserId(2L);// 分表策略
        OrderExample orderExample = new OrderExample()
                .andIdEqualTo(195416775580001L);
        List<Order> affect = orderRepository.selectByExample(query,orderExample);
        log.info("order1={}", affect);
    }

}
