package com.example.domain;

import io.github.simple.dynamodb.processor.DynamodbColumnDefinition;
import io.github.simple.jdbc.processor.SimpleJdbc;
import io.github.simple.jdbc.processor.domain.DialectEnums;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Table;

@SimpleJdbc(dialect = DialectEnums.DYNAMODB)
@Data
@Accessors(chain = true)
@Table(name = "photo")
public class Photo {

    @Column(name = "id", columnDefinition = DynamodbColumnDefinition.RANGEKEY)
    private Long id;

    @Column(name = "userId", columnDefinition = DynamodbColumnDefinition.HASHKEY)
    private String userId;

    @Column(name = "photoUrl", columnDefinition = DynamodbColumnDefinition.ATTRIBUTE)
    private String photoUrl;

}
