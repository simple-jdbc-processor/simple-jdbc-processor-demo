package com.example.domain;

import io.github.simple.jdbc.processor.SimpleJdbc;
import io.github.simple.jdbc.processor.domain.DialectEnums;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;


/**
 * Mongodb collection.
 */
@SimpleJdbc(dataSource = "simple_db", dialect = DialectEnums.MONGO)
@Getter
@Setter
@ToString
@Accessors(chain = true)
@Table(name = "content") //指定 mongo collection
public class Content {

    /**
     * 指定主键 @Id或@BsonId.
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

}
