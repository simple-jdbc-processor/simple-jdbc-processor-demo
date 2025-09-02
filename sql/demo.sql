/*
 Navicat Premium Data Transfer

 Source Server         : localhost-exchange
 Source Server Type    : MySQL
 Source Server Version : 80043 (8.0.43)
 Source Host           : localhost:3306
 Source Schema         : demo

 Target Server Type    : MySQL
 Target Server Version : 80043 (8.0.43)
 File Encoding         : 65001

 Date: 02/09/2025 14:07:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_balance
-- ----------------------------
DROP TABLE IF EXISTS `tb_balance`;
CREATE TABLE `tb_balance` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `balance` decimal(36,18) DEFAULT NULL COMMENT '余额',
  `frozen` decimal(36,18) DEFAULT NULL COMMENT '冻结金额',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of tb_balance
-- ----------------------------
BEGIN;
INSERT INTO `tb_balance` (`id`, `balance`, `frozen`, `create_time`, `update_time`) VALUES (1, 3.000000000000000000, 297.000000000000000000, '2025-09-02 11:59:44', '2025-09-02 11:59:44');
INSERT INTO `tb_balance` (`id`, `balance`, `frozen`, `create_time`, `update_time`) VALUES (2, 100.000000000000000000, 0.000000000000000000, '2025-09-02 12:09:55', '2025-09-02 12:09:55');
COMMIT;

-- ----------------------------
-- Table structure for tb_order_1
-- ----------------------------
DROP TABLE IF EXISTS `tb_order_1`;
CREATE TABLE `tb_order_1` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `amount` decimal(36,18) NOT NULL COMMENT '订单金额',
  `create_time` timestamp NOT NULL COMMENT '创建时间',
  `update_time` timestamp NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=195416775580002 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `tb_order_2`;
CREATE TABLE `tb_order_2` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `amount` decimal(36,18) NOT NULL COMMENT '订单金额',
  `create_time` timestamp NOT NULL COMMENT '创建时间',
  `update_time` timestamp NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(64) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `nickname` varchar(255) NOT NULL COMMENT '昵称',
  `status` varchar(32) NOT NULL COMMENT '用户状态',
  `create_time` timestamp NOT NULL COMMENT '创建时间',
  `update_time` timestamp NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of tb_user
-- ----------------------------
BEGIN;
INSERT INTO `tb_user` (`id`, `username`, `password`, `nickname`, `status`, `create_time`, `update_time`) VALUES (1, 'dGVzdA==', '479224bf-be05-4094-a9cb-7acb25f861eb', '538a9f06-5f84-463b-9d3e-2efddc87ac31', 'NORMAL', '2025-09-02 11:56:58', '2025-09-02 11:56:58');
INSERT INTO `tb_user` (`id`, `username`, `password`, `nickname`, `status`, `create_time`, `update_time`) VALUES (2, 'dGVzdA==', '089867db-1150-474d-b7ed-2bcdc69bf418', '9199ea53-805b-437e-8ee1-7d06a42318b6', 'NORMAL', '2025-09-02 12:09:44', '2025-09-02 12:09:44');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
