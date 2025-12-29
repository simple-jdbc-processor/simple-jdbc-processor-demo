package com.example.service;

import com.example.Application;
import com.example.domain.Product;
import com.example.domain.ProductExample;
import com.example.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@Transactional  // 添加事务管理，确保测试后数据回滚
@Slf4j
public class ProductServiceTest {

    @Autowired
    private ProductRepository productRepository;

    // 测试数据
    private static final Long TEST_PRODUCT_ID_1 = 1001L;
    private static final Long TEST_PRODUCT_ID_2 = 1002L;
    private static final String TEST_TITLE_1 = "测试标题1";
    private static final String TEST_TITLE_2 = "测试标题2";
    private static final String TEST_CONTENT_1 = "测试内容1";
    private static final String TEST_CONTENT_2 = "测试内容2";
    private static final BigDecimal TEST_PRICE = new BigDecimal("10.00");
    private static final List<String> TEST_TAGS_1 = Arrays.asList("tag1", "tag2");
    private static final List<String> TEST_TAGS_2 = Arrays.asList("tag3");
    private static final List<Double> TEST_LOCATION = Arrays.asList(0.0, 0.0);

    private AtomicLong idGenerator;
    private List<Long> testProductIds = new ArrayList<>();

    @BeforeEach
    void setUp() throws InterruptedException {
        idGenerator = new AtomicLong(System.nanoTime());
        testProductIds.clear();

        Thread.sleep(1100L);
        // 清理测试数据
        ProductExample deleteExample = ProductExample.create()
                .andIdIn(Arrays.asList(TEST_PRODUCT_ID_1, TEST_PRODUCT_ID_2));
        productRepository.deleteByExample(deleteExample);
        
        log.info("测试数据清理完成");
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        // 清理测试数据
        if (!testProductIds.isEmpty()) {
            Thread.sleep(1100l);
            ProductExample deleteExample = ProductExample.create()
                    .andIdIn(testProductIds);
            productRepository.deleteByExample(deleteExample);
            testProductIds.clear();
        }
        log.info("测试数据清理完成");
    }

    @Test
    void testInsert() {
        // 准备测试数据
        Product product = new Product()
                .setId(TEST_PRODUCT_ID_1)
                .setTitle(TEST_TITLE_1)
                .setContent(TEST_CONTENT_1)
                .setPrice(TEST_PRICE)
                .setLocation(TEST_LOCATION)
                .setTags(TEST_TAGS_1)
                .setCreateTime(new Date());
        
        // 记录测试ID以便清理
        testProductIds.add(TEST_PRODUCT_ID_1);
        
        // 执行操作
        productRepository.insert(product);
        
        // 验证数据是否正确插入
        Product insertedProduct = productRepository.selectByPrimaryKey(TEST_PRODUCT_ID_1);
        assertNotNull(insertedProduct, "插入的产品记录应存在");
        assertEquals(TEST_PRODUCT_ID_1, insertedProduct.getId());
        assertEquals(TEST_TITLE_1, insertedProduct.getTitle());
        assertEquals(TEST_CONTENT_1, insertedProduct.getContent());
        assertEquals(TEST_PRICE, insertedProduct.getPrice());
        assertEquals(TEST_TAGS_1, insertedProduct.getTags());
        
        log.info("testInsert 测试通过");
    }

    @Test
    void testInsertBatch() {
        // 准备测试数据
        Product product1 = new Product()
                .setId(TEST_PRODUCT_ID_1)
                .setTitle(TEST_TITLE_1)
                .setContent(TEST_CONTENT_1)
                .setPrice(TEST_PRICE)
                .setLocation(TEST_LOCATION)
                .setTags(TEST_TAGS_1)
                .setCreateTime(new Date());
        
        Product product2 = new Product()
                .setId(TEST_PRODUCT_ID_2)
                .setTitle(TEST_TITLE_2)
                .setContent(TEST_CONTENT_2)
                .setPrice(TEST_PRICE)
                .setLocation(TEST_LOCATION)
                .setTags(TEST_TAGS_2)
                .setCreateTime(new Date());
        
        List<Product> products = Arrays.asList(product1, product2);
        
        // 记录测试ID以便清理
        testProductIds.add(TEST_PRODUCT_ID_1);
        testProductIds.add(TEST_PRODUCT_ID_2);
        
        // 执行操作
        productRepository.insertBatch(products);
        
        // 验证数据是否正确插入
        Product insertedProduct1 = productRepository.selectByPrimaryKey(TEST_PRODUCT_ID_1);
        Product insertedProduct2 = productRepository.selectByPrimaryKey(TEST_PRODUCT_ID_2);
        
        assertNotNull(insertedProduct1, "批量插入的第一个产品记录应存在");
        assertNotNull(insertedProduct2, "批量插入的第二个产品记录应存在");
        
        assertEquals(TEST_PRODUCT_ID_1, insertedProduct1.getId());
        assertEquals(TEST_TITLE_1, insertedProduct1.getTitle());
        assertEquals(TEST_CONTENT_1, insertedProduct1.getContent());
        
        assertEquals(TEST_PRODUCT_ID_2, insertedProduct2.getId());
        assertEquals(TEST_TITLE_2, insertedProduct2.getTitle());
        assertEquals(TEST_CONTENT_2, insertedProduct2.getContent());
        
        log.info("testInsertBatch 测试通过");
    }

    @Test
    void testSelectByPrimaryKey() throws InterruptedException {
        // 首先插入一条测试数据
        Product product = new Product()
                .setId(TEST_PRODUCT_ID_1)
                .setTitle(TEST_TITLE_1)
                .setContent(TEST_CONTENT_1)
                .setPrice(TEST_PRICE)
                .setLocation(TEST_LOCATION)
                .setTags(TEST_TAGS_1)
                .setCreateTime(new Date());
        productRepository.insert(product);
        Thread.sleep(1100L);
        testProductIds.add(TEST_PRODUCT_ID_1);
        
        // 执行查询操作
        Product result = productRepository.selectByPrimaryKey(TEST_PRODUCT_ID_1);
        
        // 验证结果
        assertNotNull(result, "查询到的产品记录不应为空");
        assertEquals(TEST_PRODUCT_ID_1, result.getId());
        assertEquals(TEST_TITLE_1, result.getTitle());
        assertEquals(TEST_CONTENT_1, result.getContent());
        assertEquals(TEST_PRICE, result.getPrice());
        assertEquals(TEST_TAGS_1, result.getTags());
        
        log.info("testSelectByPrimaryKey 测试通过");
    }

    @Test
    void testDeleteByPrimaryKey() throws InterruptedException {
        // 首先插入一条测试数据
        Product product = new Product()
                .setId(TEST_PRODUCT_ID_1)
                .setTitle(TEST_TITLE_1)
                .setContent(TEST_CONTENT_1)
                .setPrice(TEST_PRICE)
                .setLocation(TEST_LOCATION)
                .setTags(TEST_TAGS_1)
                .setCreateTime(new Date());
        productRepository.insert(product);
        Thread.sleep(3000L);
        
        // 执行删除操作
        int affectRows = productRepository.deleteByPrimaryKey(TEST_PRODUCT_ID_1);
        
        // 验证结果
        assertEquals(1, affectRows, "删除操作应影响1行数据");
        Thread.sleep(1100L);

        // 验证数据是否已删除
        Product deletedProduct = productRepository.selectByPrimaryKey(TEST_PRODUCT_ID_1);
        assertNull(deletedProduct, "删除的产品记录不应存在");
        
        log.info("testDeleteByPrimaryKey 测试通过");
    }

    @Test
    void testSelectByExample() throws InterruptedException {
        // 首先插入几条测试数据
        Product product1 = new Product()
                .setId(TEST_PRODUCT_ID_1)
                .setTitle(TEST_TITLE_1)
                .setContent(TEST_CONTENT_1)
                .setPrice(TEST_PRICE)
                .setLocation(TEST_LOCATION)
                .setTags(TEST_TAGS_1)
                .setCreateTime(new Date());
        
        Product product2 = new Product()
                .setId(TEST_PRODUCT_ID_2)
                .setTitle(TEST_TITLE_2)
                .setContent(TEST_CONTENT_2)
                .setPrice(TEST_PRICE)
                .setLocation(TEST_LOCATION)
                .setTags(TEST_TAGS_2)
                .setCreateTime(new Date());
        
        productRepository.insert(product1);
        productRepository.insert(product2);
        // es 数据刷入有延迟，休眠1秒
        Thread.sleep(1100L);
        
        testProductIds.add(TEST_PRODUCT_ID_1);
        testProductIds.add(TEST_PRODUCT_ID_2);
        
        // 准备查询条件
        ProductExample example = ProductExample.create()
                .andTagsIn(Arrays.asList("tag1"));
        
        // 执行查询操作
        List<Product> results = productRepository.selectByExample(example);
        
        // 验证结果
        assertNotNull(results, "查询结果不应为空");
        assertFalse(results.isEmpty(), "查询应返回至少一条记录");
        
        // 验证每条记录都包含tag1
        for (Product product : results) {
            assertNotNull(product.getTags());
            assertTrue(product.getTags().contains("tag1"), "查询结果应包含tag1标签");
        }
        
        log.info("testSelectByExample 测试通过");
    }

    @Test
    void testDeleteByExample() throws InterruptedException {
        // 首先插入几条测试数据
        Product product1 = new Product()
                .setId(TEST_PRODUCT_ID_1)
                .setTitle(TEST_TITLE_1)
                .setContent(TEST_CONTENT_1)
                .setPrice(TEST_PRICE)
                .setLocation(TEST_LOCATION)
                .setTags(TEST_TAGS_1)
                .setCreateTime(new Date());
        
        Product product2 = new Product()
                .setId(TEST_PRODUCT_ID_2)
                .setTitle(TEST_TITLE_2)
                .setContent(TEST_CONTENT_2)
                .setPrice(TEST_PRICE)
                .setLocation(TEST_LOCATION)
                .setTags(TEST_TAGS_2)
                .setCreateTime(new Date());
        
        productRepository.insert(product1);
        productRepository.insert(product2);
        Thread.sleep(1100L);

        // 准备删除条件
        ProductExample example = ProductExample.create()
                .andTagsIn(Arrays.asList("tag1"));
        
        // 执行删除操作
        int affectRows = productRepository.deleteByExample(example);
        
        // 验证结果
        assertEquals(1, affectRows, "删除操作应影响1行数据");
        
        // 验证数据是否已删除
        Product deletedProduct = productRepository.selectByPrimaryKey(TEST_PRODUCT_ID_1);
        assertNull(deletedProduct, "符合条件的产品记录应被删除");
        
        // 验证不符合条件的数据是否保留
        Product remainingProduct = productRepository.selectByPrimaryKey(TEST_PRODUCT_ID_2);
        assertNotNull(remainingProduct, "不符合条件的产品记录应保留");
        
        log.info("testDeleteByExample 测试通过");
    }

    @Test
    void testUpdateByPrimaryKey() throws InterruptedException {
        // 首先插入一条测试数据
        Product product = new Product()
                .setId(TEST_PRODUCT_ID_1)
                .setTitle(TEST_TITLE_1)
                .setContent(TEST_CONTENT_1)
                .setPrice(TEST_PRICE)
                .setLocation(TEST_LOCATION)
                .setTags(TEST_TAGS_1)
                .setCreateTime(new Date());
        productRepository.insert(product);
        testProductIds.add(TEST_PRODUCT_ID_1);

        Thread.sleep(1100L);

        // 准备更新数据
        String updatedTitle = "更新后的标题";
        Product updateProduct = new Product()
                .setId(TEST_PRODUCT_ID_1)
                .setTitle(updatedTitle);
        
        // 执行更新操作
        productRepository.updateByPrimaryKeySelective(updateProduct);
        Thread.sleep(1100L);

        // 验证数据是否正确更新
        Product updatedProduct = productRepository.selectByPrimaryKey(TEST_PRODUCT_ID_1);
        assertNotNull(updatedProduct, "更新后的产品记录应存在");
        assertEquals(updatedTitle, updatedProduct.getTitle(), "产品标题应被正确更新");
        assertEquals(TEST_CONTENT_1, updatedProduct.getContent(), "未更新的字段应保持不变");
        
        log.info("testUpdateByPrimaryKey 测试通过");
    }

    @Test
    void testUpdateByExample() throws InterruptedException {
        // 首先插入几条测试数据
        Product product1 = new Product()
                .setId(TEST_PRODUCT_ID_1)
                .setTitle(TEST_TITLE_1)
                .setContent(TEST_CONTENT_1)
                .setPrice(TEST_PRICE)
                .setLocation(TEST_LOCATION)
                .setTags(TEST_TAGS_1)
                .setCreateTime(new Date());
        
        Product product2 = new Product()
                .setId(TEST_PRODUCT_ID_2)
                .setTitle(TEST_TITLE_2)
                .setContent(TEST_CONTENT_1) // 与product1相同的content
                .setPrice(TEST_PRICE)
                .setLocation(TEST_LOCATION)
                .setTags(TEST_TAGS_2)
                .setCreateTime(new Date());
        
        productRepository.insert(product1);
        productRepository.insert(product2);
        Thread.sleep(1100L);

        testProductIds.add(TEST_PRODUCT_ID_1);
        testProductIds.add(TEST_PRODUCT_ID_2);
        
        // 准备更新数据
        String updatedTitle = "批量更新的标题";
        Product updateProduct = new Product()
                .setTitle(updatedTitle);
        
        // 准备更新条件
        ProductExample example = ProductExample.create()
                .andTagsIn(TEST_TAGS_1);
        
        // 执行更新操作
        int affectRows = productRepository.updateByExampleSelective(updateProduct, example);
        Thread.sleep(1100L);

        // 验证结果
        assertEquals(1, affectRows, "更新操作应影响1行数据");
        
        // 验证数据是否正确更新
        Product updatedProduct1 = productRepository.selectByPrimaryKey(TEST_PRODUCT_ID_1);

        assertEquals(updatedTitle, updatedProduct1.getTitle(), "第一条产品标题应被正确更新");

        log.info("testUpdateByExample 测试通过");
    }

    @Test
    void testSelectNonExistentProduct() {
        // 尝试查询不存在的产品
        Product result = productRepository.selectByPrimaryKey(-999L);
        
        // 验证结果
        assertNull(result, "查询不存在的产品应返回null");
        
        log.info("testSelectNonExistentProduct 测试通过");
    }

    @Test
    void testDeleteNonExistentProduct() {
        // 尝试删除不存在的产品
        int affectRows = productRepository.deleteByPrimaryKey(-999L);
        
        // 验证结果
        assertEquals(0, affectRows, "删除不存在的产品应返回0");
        
        log.info("testDeleteNonExistentProduct 测试通过");
    }
}