package com.example.domain;

import io.github.simple.jdbc.processor.SimpleJdbc;
import io.github.simple.jdbc.processor.domain.DialectEnums;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.bson.BsonType;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.codecs.pojo.annotations.BsonRepresentation;

import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


/**
 * Mongodb collection.
 */
@SimpleJdbc(dataSource = "simple_db", tableName = "content", dialect = DialectEnums.MONGO)
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Content {

    /**
     * 指定主键
     */
    @BsonId
    private Long id;

    @BsonProperty
    private String title;

    @BsonProperty
    private String content;

    @BsonProperty
    private String name;

    @BsonProperty
    private Integer age;

    @BsonProperty
    private BigDecimal amount;

    @BsonProperty("created_time")
    private Date createTime;

    @BsonProperty("update_time")
    private LocalDateTime updateTime;

    @BsonProperty
    private List<String> tags;

}
