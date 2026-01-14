package com.example.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import io.github.simple.jdbc.processor.SimpleJdbc;
import lombok.Data;

@Data
@DynamoDBTable(tableName = "photo")
public class Photo {

    @DynamoDBHashKey
    private String userId;

    @DynamoDBRangeKey
    private String sortKey;

    @DynamoDBIndexHashKey
    private String secondIndexHashKey;

    @DynamoDBIndexRangeKey
    private String secondIndexRangeKey;

    @DynamoDBAttribute
    private String photoUrl;

}
