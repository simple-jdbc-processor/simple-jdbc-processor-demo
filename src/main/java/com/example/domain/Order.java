package com.example.domain;

import io.github.simple.jdbc.processor.SimpleJdbc;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@SimpleJdbc(shardTable = true, //启用分表
        slaveDataSources = {"slave1DataSource", "slave2DataSource"}, //读写分离,
        auditSql = true
)
@Getter
@Setter
@ToString
@Accessors(chain = true)
@Entity
@Table(name = "tb_order")
public class Order {

    /**
     * 订单ID.
     */
    @Id
    @Column(name = "id", columnDefinition = "BIGINT", nullable = false)
    private Long id;

    /**
     * 用户ID.
     */
    @Column(name = "user_id", columnDefinition = "BIGINT", nullable = false)
    private Long userId;

    /**
     * 订单金额.
     */
    @Column(name = "amount", columnDefinition = "DECIMAL", nullable = false, length = 36, scale = 18)
    private java.math.BigDecimal amount;

    /**
     * 创建时间.
     */
    @Column(name = "create_time", columnDefinition = "TIMESTAMP", nullable = false)
    private java.util.Date createTime;

    /**
     * 更新时间.
     */
    @Column(name = "update_time", columnDefinition = "TIMESTAMP", nullable = false)
    private java.util.Date updateTime;


}
