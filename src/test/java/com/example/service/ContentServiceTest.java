package com.example.service;

import com.example.Application;
import com.example.domain.Content;
import com.example.domain.ContentExample;
import com.example.repository.ContentRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@Transactional  // 添加事务管理，确保测试后数据回滚
@Slf4j
public class ContentServiceTest {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private MongoDatabaseFactory mongoDatabaseFactory;

    // 测试数据
    private static final Long TEST_CONTENT_ID_1 = 1001L;
    private static final Long TEST_CONTENT_ID_2 = 1002L;
    private static final String TEST_NAME_1 = "zhangsan";
    private static final String TEST_NAME_2 = "lisi";
    private static final String TEST_TITLE = "测试标题";
    private static final String TEST_CONTENT = "测试内容";
    private static final Integer TEST_AGE = 10;
    private static final BigDecimal TEST_AMOUNT = new BigDecimal("100.00");
    private static final Integer INCREMENT_VALUE = 3;

    @BeforeEach
    void setUp() {
        // 清理测试数据
        ContentExample deleteExample = ContentExample.create()
                .andIdIn(Arrays.asList(TEST_CONTENT_ID_1, TEST_CONTENT_ID_2));
        contentRepository.deleteByExample(deleteExample);
        
        // 准备测试数据
        Content content1 = new Content()
                .setId(TEST_CONTENT_ID_1)
                .setName(TEST_NAME_1)
                .setTitle(TEST_TITLE)
                .setAge(TEST_AGE)
                .setContent(TEST_CONTENT)
                .setAmount(TEST_AMOUNT)
                .setCreateTime(new Date())
                .setUpdateTime(LocalDateTime.now());
        Content content2 = new Content()
                .setId(TEST_CONTENT_ID_2)
                .setName(TEST_NAME_2)
                .setTitle(TEST_TITLE)
                .setAge(TEST_AGE)
                .setContent(TEST_CONTENT)
                .setAmount(TEST_AMOUNT)
                .setCreateTime(new Date())
                .setUpdateTime(LocalDateTime.now());
        
        contentRepository.insert(content1);
        contentRepository.insert(content2);
        log.info("测试数据准备完成");
    }

    @AfterEach
    void tearDown() {
        // 清理测试数据
        ContentExample deleteExample = ContentExample.create()
                .andIdIn(Arrays.asList(TEST_CONTENT_ID_1, TEST_CONTENT_ID_2));
        contentRepository.deleteByExample(deleteExample);
        log.info("测试数据清理完成");
    }

    @Test
    void testSelectByPrimaryKey() {
        // 执行操作
        Content content = contentRepository.selectByPrimaryKey(TEST_CONTENT_ID_1);
        
        // 验证结果
        assertNotNull(content, "查询到的内容记录不应为空");
        assertEquals(TEST_CONTENT_ID_1, content.getId());
        assertEquals(TEST_NAME_1, content.getName());
        assertEquals(TEST_TITLE, content.getTitle());
        
        log.info("testSelectByPrimaryKey 测试通过");
    }

    @Test
    void testInsert() {
        // 准备测试数据
        Long newContentId = 2000L;
        Content newContent = new Content()
                .setId(newContentId)
                .setName("wangwu")
                .setTitle("新标题")
                .setAge(20)
                .setContent("新内容")
                .setAmount(new BigDecimal("200"))
                .setCreateTime(new Date())
                .setUpdateTime(LocalDateTime.now());
        
        // 执行操作
        contentRepository.insert(newContent);
        
        // 验证数据是否正确插入
        Content insertedContent = contentRepository.selectByPrimaryKey(newContentId);
        assertNotNull(insertedContent, "插入的内容记录应存在");
        assertEquals(newContentId, insertedContent.getId());
        assertEquals("wangwu", insertedContent.getName());
        assertEquals(new BigDecimal("200"), insertedContent.getAmount());
        
        log.info("testInsert 测试通过");
        contentRepository.deleteByPrimaryKey(newContentId);
    }

    @Test
    void testSelectByExample() {
        // 准备查询条件
        ContentExample query = ContentExample.create()
                .andAgeEqualTo(TEST_AGE)
                .limit(2);
        
        // 执行操作
        List<Content> contents = contentRepository.selectByExample(query);
        
        // 验证结果
        assertNotNull(contents, "查询结果不应为空");
        assertFalse(contents.isEmpty(), "查询应返回至少一条记录");
        assertEquals(2, contents.size(), "查询应返回2条记录");
        
        // 验证每条记录的年龄是否符合条件
        for (Content content : contents) {
            assertEquals(TEST_AGE, content.getAge());
        }
        
        log.info("testSelectByExample 测试通过");
    }

    @Test
    void testSelectByExampleWithSort() {
        // 准备查询条件
        ContentExample query = ContentExample.create()
                .desc("_id")
                .limit(1);
        
        // 执行操作
        List<Content> contents = contentRepository.selectByExample(query);
        
        // 验证结果
        assertNotNull(contents, "查询结果不应为空");
        assertFalse(contents.isEmpty(), "查询应返回至少一条记录");
        assertEquals(1, contents.size(), "查询应返回1条记录");
        
        log.info("testSelectByExampleWithSort 测试通过");
    }

    @Test
    void testIncrement() {
        // 准备查询条件
        ContentExample query = ContentExample.create()
                .andNameEqualTo(TEST_NAME_2);
        
        // 执行操作
        int result = contentRepository.incrementByExample(query, "age", INCREMENT_VALUE);
        
        // 验证结果
        assertEquals(1, result, "更新操作应返回1表示成功");
        
        // 验证值是否正确增加
        Content updatedContent = contentRepository.selectByPrimaryKey(TEST_CONTENT_ID_2);
        Integer expectedAge = TEST_AGE + INCREMENT_VALUE;
        assertEquals(expectedAge, updatedContent.getAge());
        
        log.info("testIncrement 测试通过");
    }

    @Test
    void testDeleteByPrimaryKey() {
        // 执行操作
        int result = contentRepository.deleteByPrimaryKey(TEST_CONTENT_ID_1);
        
        // 验证结果
        assertEquals(1, result, "删除操作应返回1表示成功");
        
        // 验证数据是否已删除
        Content deletedContent = contentRepository.selectByPrimaryKey(TEST_CONTENT_ID_1);
        assertNull(deletedContent, "删除的记录不应存在");
        
        log.info("testDeleteByPrimaryKey 测试通过");
    }

    @Test
    void testSelectNonExistentContent() {
        // 尝试查询不存在的内容
        Content content = contentRepository.selectByPrimaryKey(-999L);
        
        // 验证结果
        assertNull(content, "查询不存在的内容应返回null");
        
        log.info("testSelectNonExistentContent 测试通过");
    }

    @Test
    void testDeleteByExample() {
        // 准备删除条件
        ContentExample deleteExample = ContentExample.create()
                .andAgeEqualTo(TEST_AGE);
        
        // 执行操作
        int result = contentRepository.deleteByExample(deleteExample);
        
        // 验证结果
        assertTrue(result >= 1, "删除操作应返回至少1表示成功");
        
        // 验证数据是否已删除
        ContentExample queryExample = ContentExample.create()
                .andAgeEqualTo(TEST_AGE);
        List<Content> remainingContents = contentRepository.selectByExample(queryExample);
        assertTrue(remainingContents.isEmpty(), "符合条件的记录应全部删除");
        
        log.info("testDeleteByExample 测试通过");
    }
}