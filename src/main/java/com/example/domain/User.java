package com.example.domain;

import com.example.enums.UserStatus;
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
@Table(name = "tb_user")
public class User {

    /**
     *  主键.
     */
    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "BIGINT", nullable = false)
    private Long id;

    /**
     *  用户名.
     */
    @Column(name = "username", columnDefinition = "VARCHAR", nullable = false, length = 64)
    private String username;

    /**
     *  密码.
     */
    @Column(name = "password", columnDefinition = "VARCHAR", nullable = false)
    private String password;

    /**
     *  昵称.
     */
    @Column(name = "nickname", columnDefinition = "VARCHAR", nullable = false)
    private String nickname;
    /**
     *  昵称.
     */
    @Column(name = "status", columnDefinition = "VARCHAR", nullable = false)
    private UserStatus status;

    /**
     *  创建时间.
     */
    @Column(name = "create_time", columnDefinition = "TIMESTAMP", nullable = false)
    private java.util.Date createTime;

    /**
     *  更新时间.
     */
    @Column(name = "update_time", columnDefinition = "TIMESTAMP", nullable = false)
    private java.util.Date updateTime;


}
