package com.example.repository;

import com.example.domain.Order;
import com.example.domain.OrderShardSimpleJdbcRepository;
import com.example.domain.OrderSimpleJdbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository extends OrderShardSimpleJdbcRepository {

    /**
     * 分表策略.
     */
    @Override
    protected String getTableName(Order t) {
        return "tb_order_" + (t.getUserId() % 2 + 1);
    }
}
