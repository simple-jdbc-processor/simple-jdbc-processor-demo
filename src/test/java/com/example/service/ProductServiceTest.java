package com.example.service;


import com.example.Application;
import com.example.domain.Product;
import com.example.domain.ProductExample;
import com.example.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootTest(classes = Application.class)
@Slf4j
public class ProductServiceTest {

    @Autowired
    private ProductRepository productRepository;


    @Test
    public void testInsert() {
        Product product = new Product();
        product.setId(System.currentTimeMillis());
        product.setTitle("title");
        product.setContent("content");
        product.setPrice(BigDecimal.TEN);
        product.setLocation(Arrays.asList(0.0, 0.0));
        product.setCreateTime(new Date());
        productRepository.insert(product);
    }

    @Test
    public void testInsertBatch() {
        Product product = new Product();
        product.setId(System.currentTimeMillis());
        product.setTitle("title");
        product.setContent("content");
        product.setPrice(BigDecimal.TEN);
        product.setLocation(Arrays.asList(0.0, 0.0));
        product.setTags(Arrays.asList("tag1", "tag2"));
        product.setCreateTime(new Date());
        Product product2 = new Product();
        product2.setId(System.currentTimeMillis() + 1);
        product2.setTitle("title2");
        product2.setContent("content2");
        product2.setPrice(BigDecimal.TEN);
        product2.setLocation(Arrays.asList(0.0, 0.0));
        product2.setCreateTime(new Date());
        productRepository.insertBatch(Arrays.asList(product, product2));
    }

    @Test
    public void testSelectByPrimaryKey() {
        Product product = productRepository.selectByPrimaryKey(1757067124519L);
        log.info("product:{}", product);
    }

    @Test
    public void testDeleteByPrimaryKey() {
        int affect = productRepository.deleteByPrimaryKey(1757067259282L);
        log.info("product:{}", affect);
    }

    @Test
    public void testSelectByExample() {
        ProductExample example = new ProductExample()
                .andTagsIn(Arrays.asList("tag1"));
        List<Product> products = productRepository.selectByExample(example);
        log.info("products:{}", products);
    }

    @Test
    public void testDeleteByExample() {
        ProductExample example = new ProductExample()
                .andTagsIn(Arrays.asList("tag1"));
        int products = productRepository.deleteByExample(example);
        log.info("products:{}", products);
    }

    @Test
    public void testUpdateByPrimaryKey() {
        Product product = productRepository.selectByPrimaryKey(1757132060639L);
        Product newProduct = new Product();
        newProduct.setId(product.getId());
        newProduct.setTitle("updated title");
        productRepository.updateByPrimaryKeySelective(newProduct);
    }

    @Test
    public void testUpdateByExample() {
        Product product = productRepository.selectByPrimaryKey(1757132060639L);
        Product newProduct = new Product();
        newProduct.setId(product.getId());
        newProduct.setTitle("updated title2");
        ProductExample example = new ProductExample()
                .andContentEqualTo("content2");
        productRepository.updateByExampleSelective(newProduct, example);
    }
}
