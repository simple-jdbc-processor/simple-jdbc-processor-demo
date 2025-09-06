package com.example.service;


import com.example.Application;
import com.example.domain.Content;
import com.example.domain.ContentExample;
import com.example.repository.ContentRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.codecs.pojo.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.MongoDatabaseFactory;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest(classes = Application.class)
@Slf4j
public class ContentServiceTest {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private MongoDatabaseFactory mongoDatabaseFactory;

    @Test
    public void testSelectByPrimaryKey() {
        Content content = contentRepository.selectByPrimaryKey(1757059659890L);
        log.info("content: {}", content);
    }

    @Test
    public void testInsert() {
        Content content = new Content();
        content.setId(System.currentTimeMillis());
        content.setName("lisi");
        content.setTitle("title");
        content.setAge(10);
        content.setContent("content");
        content.setAmount(BigDecimal.TEN);
        content.setUpdateTime(LocalDateTime.now());
        content.setCreateTime(new Date());
        contentRepository.insert(content);
        log.info("content: {}", content);
        Content content1 = contentRepository.selectByPrimaryKey(content.getId());
        log.info("content1: {}", content1);
    }

    @Test
    public void testSelectByExample() {
        ContentExample query = ContentExample.create()
                .andAgeEqualTo(10)
                .limit(2);
        List<Content> content = contentRepository.selectByExample(query);
        log.info("content: {}", content);
    }

    @Test
    public void testSelectByExampleWithSort() {
        ContentExample query = ContentExample.create()
                .desc("_id")
                .limit(1);
        List<Content> content = contentRepository.selectByExample(query);
        log.info("content: {}", content);
    }

    @Test
    public void testIncrement() {
        ContentExample query = ContentExample.create()
                .andNameEqualTo("lisi");
        int i = contentRepository.incrementByExample(query, "age", -3);
        log.info("{}", i);
    }
}
