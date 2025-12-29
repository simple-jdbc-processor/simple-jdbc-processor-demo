package com.example.handler;

import cn.hutool.core.lang.generator.SnowflakeGenerator;
import com.example.domain.Order;
import com.example.domain.OrderSimpleJdbcDefaultTypeHandler;
import com.example.domain.User;
import com.example.domain.UserSimpleJdbcDefaultTypeHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 自定义类型处理器.
 */
@RequiredArgsConstructor
@Component
public class OrderTypeHandler extends OrderSimpleJdbcDefaultTypeHandler {

    private final SnowflakeGenerator snowflake;

    /**
     * 自定义ID生成策略.
     */
    @Override
    public void generatePrimaryKey(Order t) {
        if (t.getId() == null) {
            t.setId(snowflake.next());
        }
    }

    /**
     * 自定义ID生成策略.
     */
    @Override
    public void batchGeneratePrimaryKey(List<Order> ts) {
        for (Order t : ts) {
            if (t.getId() == null) {
                t.setId(snowflake.next());
            }
        }
    }

    /**
     * SQL审计
     */
    @Override
    public String auditSql(Logger log, String sql, List params) {
        return super.auditSql(log, sql, params);
    }

    /**
     * 打印审计SQL
     */
    @Override
    public void printAuditSql(Logger log, String sql) {
        log.info("Audit sql:  {}", sql);
    }
}
