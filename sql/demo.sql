create database if not exists `simple_db`  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;;
use `simple_db`;

CREATE TABLE `tb_balance` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `balance` decimal(36,18) DEFAULT NULL COMMENT '余额',
  `frozen` decimal(36,18) DEFAULT NULL COMMENT '冻结金额',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB ;

CREATE TABLE `tb_order_1` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `amount` decimal(36,18) NOT NULL COMMENT '订单金额',
  `create_time` timestamp NOT NULL COMMENT '创建时间',
  `update_time` timestamp NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `tb_order_2` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `amount` decimal(36,18) NOT NULL COMMENT '订单金额',
  `create_time` timestamp NOT NULL COMMENT '创建时间',
  `update_time` timestamp NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;


CREATE TABLE `tb_user` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(64) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `nickname` varchar(255) NOT NULL COMMENT '昵称',
  `status` varchar(32) NOT NULL COMMENT '用户状态',
  `create_time` timestamp NOT NULL COMMENT '创建时间',
  `update_time` timestamp NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;
