package com.example.domain;

import io.github.simple.dynamodb.processor.DynamodbColumnDefinition;
import io.github.simple.jdbc.processor.SimpleJdbc;
import io.github.simple.jdbc.processor.domain.DialectEnums;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.List;
import java.util.Map;

@SimpleJdbc(dialect = DialectEnums.DYNAMODB)
@Getter
@Setter
@ToString
@Accessors(chain = true)
@Table(name = "photo")
public class Photo {

    @Column(name = "id", columnDefinition = DynamodbColumnDefinition.RANGEKEY)
    private Long id;

    @Column(name = "userId", columnDefinition = DynamodbColumnDefinition.HASHKEY)
    private String userId;

    @Column(name = "photoUrl", columnDefinition = DynamodbColumnDefinition.ATTRIBUTE)
    private String photoUrl;

    @Column(name = "tags", columnDefinition = DynamodbColumnDefinition.ATTRIBUTE)
    private List<String> tags;

    @Column(name = "metadata", columnDefinition = DynamodbColumnDefinition.ATTRIBUTE)
    private Map<String, String> metadata;
}
