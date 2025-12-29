# Getting Started

## 核心功能
- 用户管理：用户的增删改查操作
- 产品管理：产品的增删改查操作
- 订单管理：订单的创建和查询，支持分表策略
- 余额管理：用户余额的管理
- 内容管理：内容的存储和检索

## 数据库配置
项目使用 MySQL 作为主数据库，配置了主从架构。所有数据库连接信息可在 `application.yml` 文件中找到。

## 缓存和搜索引擎配置
- Redis：用于缓存频繁访问的数据
- MongoDB：用于存储非结构化数据
- Elasticsearch：用于全文搜索和日志分析

## 分表策略
项目采用了基于用户 ID 的分表策略，通过 `getTableName` 方法根据用户 ID 计算具体的表名。

### 组件服务包含
- **Java**：25版
- **Maven**：3.9.12
- **MySQL**：主数据库，端口 3306
- **Redis**：缓存服务，端口 6379
- **MongoDB**：文档数据库，端口 27017
- **Elasticsearch**：9.x 搜索引擎，端口 9200 和 9300
- **Kibana**：9.x Elasticsearch 可视化工具，端口 5601

### 使用方法
1. 确保已安装 Docker 和 Docker Compose
2. 进入项目根目录
3. 运行以下命令启动所有依赖组件服务：

```bash
cd docker
docker-compose up -d
```

7. 导入数据

Mysql 数据导入
```SQL
create database if not exists `simple_db`  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;;
use `simple_db`;

DROP TABLE IF EXISTS `tb_balance`;
CREATE TABLE `tb_balance` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `balance` decimal(36,18) DEFAULT NULL COMMENT '余额',
  `frozen` decimal(36,18) DEFAULT NULL COMMENT '冻结金额',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB ;

DROP TABLE IF EXISTS `tb_order_1`;
CREATE TABLE `tb_order_1` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `amount` decimal(36,18) NOT NULL COMMENT '订单金额',
  `create_time` timestamp NOT NULL COMMENT '创建时间',
  `update_time` timestamp NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `tb_order_2`;
CREATE TABLE `tb_order_2` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `amount` decimal(36,18) NOT NULL COMMENT '订单金额',
  `create_time` timestamp NOT NULL COMMENT '创建时间',
  `update_time` timestamp NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

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
) ENGINE=InnoDB;
```

ES表结构创建
```
PUT /product
{
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 0,
    "refresh_interval": "1s"
  },
  "mappings": {
    "properties": {
      "id": {
        "type": "keyword"
      },
      "title": {
        "type": "text"
      },
      "content": {
        "type": "text"
      },
      "price": {
        "type": "double"
      },
      "tags": {
        "type": "keyword"
      },
      "create_time": {
        "type": "date",
        "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis"
      },
      "location": {
        "type": "geo_point"
      }
    }
  }
}
```

### 数据持久化
所有服务的数据都配置了卷挂载，确保容器重启后数据不会丢失：
- MySQL 数据：`mysql-data` 卷
- Redis 数据：`redis-data` 卷
- MongoDB 数据：`mongodb-data` 卷
- Elasticsearch 数据：`elasticsearch-data` 卷

## 运行指南
### 使用 Maven 运行
1. 确保所有外部服务已通过 Docker Compose 启动
2. 运行以下命令启动应用：

```bash
./mvnw spring-boot:run
```

### 构建和运行 JAR 文件
1. 构建项目：

```bash
./mvnw clean package
```

2. 运行生成的 JAR 文件：

```bash
java -jar target/app.jar
```

## 测试
项目包含了完整的单元测试和集成测试，位于 `src/test/java` 目录下。

### 运行测试
```bash
./mvnw test
```

## 注意事项
1. 默认配置适用于开发环境，生产环境请修改安全设置
2. MySQL 根密码：`rootpassword`
3. Redis 密码：`redispassword`
4. MongoDB 用户名：`root`，密码：`rootpassword`
5. Elasticsearch 默认关闭了安全验证，生产环境请启用

