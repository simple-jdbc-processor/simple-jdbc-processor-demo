---
name: table-to-simple-jdbc-code
description: Generates simple-jdbc-processor code (domain, repository, service, controller, test, DDL) from a MySQL CREATE TABLE statement, following the simple-jdbc-processor-demo code style. Use when the user pastes a CREATE TABLE / DDL statement, asks to scaffold code for a database table, or mentions generating domain/repository/service/controller/test code for a new table.
---

# 表结构生成 simple-jdbc-processor 代码

从 MySQL 建表语句生成本项目风格的 `domain` / `repository` / `service` / `controller` / `test` 代码。

## 工作流

复制以下清单并逐项推进：

```text
- [ ] 1. 解析 DDL：表名、字段、类型、注释、主键、是否分表
- [ ] 2. 生成 domain（@SimpleJdbc 实体）
- [ ] 3. 追加 DDL 到 src/main/resources/sql/demo.sql
- [ ] 4. 运行 ./mvnw compile 生成 {Entity}Example / {Entity}SimpleJdbcRepository
- [ ] 5. 生成 repository / service / controller
- [ ] 6. 生成 {Entity}ServiceTest
- [ ] 7. 运行 ./mvnw -Dtest={Entity}ServiceTest test（需要 MySQL 可用）
```

第 4 步必须在第 5 步之前完成，否则 `{Entity}Example`、`{Entity}SimpleJdbcRepository` 不存在，repository/service 无法编译。

## 命名与目录

| 输入 | 输出 |
| --- | --- |
| 表名 `tb_skill_template` | 类名 `SkillTemplate`（去掉 `tb_` 前缀后转大驼峰） |
| 列名 `create_time` | 字段 `createTime`（小驼峰） |
| 类名 `SkillTemplate` | 路由 `api/skill-template`（短横线） |

固定包结构，包名保持 `com.example`：

```text
src/main/java/com/example/domain/SkillTemplate.java
src/main/java/com/example/repository/SkillTemplateRepository.java
src/main/java/com/example/service/SkillTemplateService.java
src/main/java/com/example/controller/SkillTemplateController.java
src/test/java/com/example/service/SkillTemplateServiceTest.java
```

## 类型映射

| MySQL 列类型 | Java 字段类型 | `columnDefinition` | 附加属性 |
| --- | --- | --- | --- |
| `bigint` / `bigint unsigned` | `Long` | `BIGINT` | — |
| `int` / `smallint` | `Integer` | `INT` | — |
| `tinyint(1)` | `Boolean` | `TINYINT` | — |
| `decimal(36,18)` | `java.math.BigDecimal` | `DECIMAL` | `length = 36, scale = 18` |
| `varchar(64)` | `String` | `VARCHAR` | `length = 64`（255 可省略） |
| `text` / `json` | `String` | `VARCHAR` | — |
| `timestamp` / `datetime` | `java.time.LocalDateTime` | `TIMESTAMP` | — |
| `date` | `java.time.LocalDate` | `DATE` | — |
| `time` | `java.time.LocalTime` | `TIME` | — |

规则：

- `unsigned` 忽略，仍映射为 `Long` / `Integer`。
- 金额、数量一律 `BigDecimal`，禁止 `double` / `float`。
- 时间类型一律用 `java.time` 类型，不要用 `java.util.Date`：带时分秒的 `timestamp` / `datetime` 用 `LocalDateTime`，纯日期 `date` 用 `LocalDate`。
- `BigDecimal`、`LocalDateTime` 等在 domain 中写全限定名（`java.math.BigDecimal`、`java.time.LocalDateTime`），与现有实体一致。
- 赋值用 `LocalDateTime.now()` / `LocalDate.now()`，不要 `new Date()`。
- 列上有固定取值语义（如 `status varchar`）时，在 `com.example.enums` 建枚举并把字段声明为枚举类型，参考 `UserStatus`。
- 列存储逗号分隔或加密内容时，字段可用 `List<String>` 等类型，并补一个继承 `{Entity}SimpleJdbcDefaultTypeHandler` 的处理器，参考 `UserTypeHandler`。

## Domain 模板

`NOT NULL` → `nullable = false`；`AUTO_INCREMENT` 主键 → `@Id @GeneratedValue`；字段 Javadoc 取 DDL 的 `COMMENT` 原文并以 `.` 结尾。

```java
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
@Table(name = "tb_skill_template")
public class SkillTemplate {

    /**
     * 订单ID.
     */
    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "BIGINT", nullable = false)
    private Long id;

    /**
     * 订单金额.
     */
    @Column(name = "amount", columnDefinition = "DECIMAL", nullable = false, length = 36, scale = 18)
    private java.math.BigDecimal amount;

    /**
     * 创建时间.
     */
    @Column(name = "create_time", columnDefinition = "TIMESTAMP", nullable = false)
    private java.time.LocalDateTime createTime;
}
```

表名或列名命中 MySQL 关键字（`user`、`order`、`password`、`status` 等）时，改用带 `escape` 的注解：

```java
@SimpleJdbc(
        dialect = DialectEnums.MYSQL,
        escape = true // 是否转义 关键词 user-> `user`,password-> `password`
)
```

### 分表实体

用户明确要求分表（或 DDL 形如 `tb_xxx_1` / `tb_xxx_2`）时：主键只写 `@Id`（不加 `@GeneratedValue`，ID 由业务生成），并声明分表键。

```java
@SimpleJdbc(shardTable = true, //启用分表
        slaveDataSources = {"slave1DataSource", "slave2DataSource"}, //读写分离,
        auditSql = true
)
```

## Repository 模板

默认空实现，只继承生成的仓储：

```java
package com.example.repository;

import com.example.domain.SkillTemplateSimpleJdbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public class SkillTemplateRepository extends SkillTemplateSimpleJdbcRepository {

}
```

分表实体继承 `{Entity}ShardSimpleJdbcRepository` 并重写分表策略：

```java
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
```

只有生成的 CRUD 与 `Example` 表达不了的操作，才在 repository 内加自定义方法：条件更新优先 `Example.set(...)`，单行/批量增减用静态 SQL + `?` 绑定，统计用 `aggregate(...)`。禁止把外部输入拼进 SQL。参考 `BalanceRepository`。

## Service 模板

```java
package com.example.service;


import com.example.domain.SkillTemplateExample;
import io.github.simple.jdbc.processor.BaseService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class SkillTemplateService extends BaseService<SkillTemplate, Long, SkillTemplateExample> {

    private final SkillTemplateRepository skillTemplateRepository;


}
```

- 泛型第二个参数是主键类型；分表实体不继承 `BaseService`，只注入 repository（参考 `OrderService`）。
- 业务校验写在 service，持久化细节留在 repository。

## Controller 模板

```java
package com.example.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RequestMapping(value = "api/skill-template")
@RestController
public class SkillTemplateController {

    private final SkillTemplateService skillTemplateService;

    @GetMapping("{id}")
    public SkillTemplate getById(@PathVariable Long id) {
        return skillTemplateService.selectByPrimaryKey(id);
    }

}
```

只生成 `getById`，其余接口按用户需求再加。

## DDL

把建表语句原样追加到 `src/main/resources/sql/demo.sql` 末尾，保持 `CREATE TABLE if not exists` 与 `ENGINE=InnoDB` 写法。测试的 `@BeforeEach` 会执行该文件建表。

## Test 模板

参考 `BalanceServiceTest`：`@BeforeEach` 先执行 `sql/demo.sql`，再删除测试数据、插入基线数据；`@AfterEach` 清理；每个用例结尾打一行 `log.info("{方法名} 测试通过")`。

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@Transactional  // 添加事务管理，确保测试后数据回滚
@Slf4j
public class SkillTemplateServiceTest {

    @Autowired
    private SkillTemplateService skillTemplateService;

    @Autowired
    private SkillTemplateRepository skillTemplateRepository;

    // 测试数据
    private static final Long TEST_ID_1 = 900001L;

    @BeforeEach
    void setUp() throws SQLException, IOException {
        DataSource dataSource = skillTemplateRepository.getDataSource();
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             InputStream in = SkillTemplate.class.getClassLoader().getResourceAsStream("sql/demo.sql")) {
            statement.execute(StreamUtils.copyToString(in, StandardCharsets.UTF_8));
        }
        // 清理测试数据
        SkillTemplateExample deleteExample = SkillTemplateExample.create()
                .andIdIn(Arrays.asList(TEST_ID_1));
        skillTemplateRepository.deleteByExample(deleteExample);
    }

    @Test
    void testSelectByPrimaryKey() {
        // 执行操作
        SkillTemplate result = skillTemplateService.selectByPrimaryKey(TEST_ID_1);

        // 验证结果
        assertNotNull(result, "查询到的记录不应为空");
        assertEquals(TEST_ID_1, result.getId());

        log.info("testSelectByPrimaryKey 测试通过");
    }
}
```

默认覆盖的用例集合：

- `testInsertSelective`：插入后按主键读回
- `testSelectByPrimaryKey`
- `testSelectByExample`：`Example` 条件 + `limit`
- `testUpdateByPrimaryKeySelective`：选择性更新不影响其他字段
- `testDeleteByExample`：断言影响行数
- `testSelectNonExistent`：不存在主键返回 `null`

`BigDecimal` 断言先 `setScale(2, RoundingMode.DOWN)` 再比较。

## 查询风格

`{Entity}Example` 必须多行链式，`create()` 单独一行，每行一个条件，单条件也不压成一行。

```java
SkillTemplateExample query = SkillTemplateExample.create()
        .andUserIdEqualTo(userId)
        .limit(10);
```

分表实体所有 repository 调用都要额外传入含分表键的路由对象：

```java
SkillTemplate route = new SkillTemplate()
        .setUserId(userId);
List<SkillTemplate> list = skillTemplateRepository.selectByExample(route, query);
```

## 验证

Windows 用 `mvnw.cmd`，其他平台用 `./mvnw`：

```bash
./mvnw compile
./mvnw -Dtest=SkillTemplateServiceTest test
```

`./mvnw compile` 通过即说明实体注解合法、生成类可用；跑测试需要 `docker/docker-compose.yml` 里的 MySQL 已启动。

## 陷阱

- `{Entity}Example` / `{Entity}SimpleJdbcRepository` 找不到：先 `./mvnw compile`，注解处理器在 `pom.xml` 的 `annotationProcessorPaths`。
- 表名含 MySQL 关键字却没开 `escape = true`，运行时 SQL 语法错误。
- `decimal` 漏写 `length` / `scale`，精度被截断。
- 分表查询返回空：路由对象缺分表键。
- `@GeneratedValue` 加在非 `AUTO_INCREMENT` 主键上，插入拿不到 ID。
- 时间字段误用 `java.util.Date`：`Balance`、`User`、`Order` 是早期实体，新代码统一用 `LocalDateTime` / `LocalDate`。

## 项目内参考实现

| 场景 | 参考文件 |
| --- | --- |
| MySQL 普通表 | `domain/Balance.java`、`repository/BalanceRepository.java`、`service/BalanceService.java`、`service/BalanceServiceTest.java` |
| 关键字转义 + 枚举 + 类型处理器 | `domain/User.java`、`handler/UserTypeHandler.java`、`enums/UserStatus.java` |
| 分表 + 读写分离 | `domain/Order.java`、`repository/OrderRepository.java`、`service/OrderServiceTest.java` |
| 本 skill 生成结果 | `domain/SkillTemplate.java` 及同名 repository/service/controller/test |

MongoDB / Elasticsearch / DynamoDB 等非 MySQL 方言不在本 skill 范围，改用 `simple-jdbc-processor` skill。
