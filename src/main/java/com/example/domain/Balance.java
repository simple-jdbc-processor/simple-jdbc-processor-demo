package com.example.domain;

import io.github.simple.jdbc.processor.SimpleJdbc;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;

@SimpleJdbc
@Getter
@Setter
@ToString
@Accessors(chain = true)
@Entity
@Table(name = "tb_balance")
public class Balance {

    /**
     *  用户ID.
     */
    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "BIGINT", nullable = false)
    private Long id;

    /**
     *  余额.
     */
    @Column(name = "balance", columnDefinition = "DECIMAL", length = 36, scale = 18)
    private java.math.BigDecimal balance;

    /**
     *  冻结金额.
     */
    @Column(name = "frozen", columnDefinition = "DECIMAL", length = 36, scale = 18)
    private java.math.BigDecimal frozen;

    /**
     *  创建时间.
     */
    @Column(name = "create_time", columnDefinition = "TIMESTAMP")
    private java.util.Date createTime;

    /**
     *  更新时间.
     */
    @Column(name = "update_time", columnDefinition = "TIMESTAMP")
    private java.util.Date updateTime;


}
