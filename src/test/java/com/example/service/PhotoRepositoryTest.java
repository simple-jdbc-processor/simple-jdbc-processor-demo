package com.example.service;

import com.example.Application;
import com.example.domain.Photo;
import com.example.domain.PhotoExample;
import com.example.repository.PhotoRepository;
import io.github.simple.dynamodb.processor.KeyPair;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import javax.persistence.Column;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@Slf4j
public class PhotoRepositoryTest {

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private DynamoDbClient dynamoDbClient;


    @BeforeEach
    public void setUpTable() {
        String tableName = Photo.class.getAnnotation(Table.class).name();
        ListTablesResponse listTablesResponse = dynamoDbClient.listTables();
        if (listTablesResponse.tableNames().contains(tableName)) {
            return;
        }

        KeySchemaElement rangeKey = KeySchemaElement.builder()
                .attributeName("id")
                .keyType(KeyType.RANGE)
                .build();
        KeySchemaElement hashKey = KeySchemaElement.builder()
                .attributeName("userId")
                .keyType(KeyType.HASH)
                .build();
        CreateTableRequest createTableRequest = CreateTableRequest.builder()
                .tableName(tableName)
                .keySchema(Arrays.asList(hashKey, rangeKey))
                .attributeDefinitions(
                        AttributeDefinition.builder()
                                .attributeName("userId")
                                .attributeType(ScalarAttributeType.S)
                                .build(),
                        AttributeDefinition.builder()
                                .attributeName("id")
                                .attributeType(ScalarAttributeType.N)
                                .build())
                .provisionedThroughput(
                        ProvisionedThroughput.builder()
                                .readCapacityUnits(5L) // 5 个读取容量单位
                                .writeCapacityUnits(5L) // 5 个写入容量单位
                                .build()
                )
                .build();
        dynamoDbClient.createTable(createTableRequest);

    }

    @Test
    public void testInsert() {
        Photo photo = new Photo()
                .setId(1L)
                .setUserId("user1")
                .setPhotoUrl("https://example.com/photo.jpg");
        photoRepository.insert(photo);
    }

    @Test
    public void testInsertBatch() {
        Photo photo1 = new Photo()
                .setId(2L)
                .setUserId("user2")
                .setPhotoUrl("https://example.com/photo.jpg");
        Photo photo2 = new Photo()
                .setId(3L)
                .setUserId("user3")
                .setPhotoUrl("https://example.com/photo.jpg");
        photoRepository.insertBatch(Arrays.asList(photo1, photo2));
    }

    @Test
    public void testSelectByPrimaryKey() {
        Photo photo = photoRepository.selectByPrimaryKey(
                new KeyPair("user1", 1L)
        );
        log.info("photo: {}", photo);
    }

    @Test
    public void testSelectByPrimaryKeys() {
        KeyPair keyPair1 = new KeyPair("user1", 2L);
        KeyPair keyPair2 = new KeyPair("user1", 3L);

        List<Photo> photo = photoRepository.selectByPrimaryKeys(Arrays.asList(keyPair1, keyPair2));
        log.info("photo: {}", photo);
    }

    @Test
    public void testSelectByExample() {
        PhotoExample photoExample = new PhotoExample()
                .andUserIdEqualTo("user1")
                .andIdGreaterThan(1L);
        List<Photo> photos = photoRepository.selectByExample(photoExample);
        log.info("photos: {}", photos);
    }

    @Test
    public void testSelectByExampleLastEvaluatedKey() {
        PhotoExample photoExample = new PhotoExample()
                .andUserIdEqualTo("user1")
                .limit(1);
        while (true) {
            List<Photo> photos = photoRepository.selectByExample(photoExample);
            if (photos.isEmpty()) {
                return;
            }
            log.info("photos: {}", photos);
        }
    }

    @Test
    public void testSelectColumns() {
        PhotoExample photoExample = new PhotoExample()
                .columns("userId")
                .andUserIdEqualTo("user1")
                .andIdEqualTo(1L);
        List<Photo> photos = photoRepository.selectByExample(photoExample);
        log.info("photos: {}", photos);
    }

    @Test
    public void testDeleteByPrimaryKey() {
        photoRepository.deleteByPrimaryKey(new KeyPair("user1", 1L));
    }

    @Test
    public void testDeleteByPrimaryKeys() {
        photoRepository.deleteByPrimaryKeys(
                Arrays.asList(
                        new KeyPair("user1", 1L),
                        new KeyPair("user2", 2L),
                        new KeyPair("user3", 3L)
                )
        );
    }

}