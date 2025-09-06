package com.example.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonKey;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.simple.jdbc.processor.SimpleJdbc;
import io.github.simple.jdbc.processor.domain.DialectEnums;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * ElasticSearch Index.
 */
@SimpleJdbc(dialect = DialectEnums.ELASTICSEARCH)
@Getter
@Setter
@ToString
@Accessors(chain = true)
@Table(name = "product") // indexName
public class Product {

    /**
     * @Id 标记主键.
     */
    @Id
    private Long id;

    @JsonProperty
    private String title;

    @JsonProperty
    private String content;

    @JsonProperty
    private BigDecimal price;

    @JsonProperty
    private List<String> tags;

    @JsonProperty
    private List<Double> location;

    /**
     * 自定义属性名.
     */
    @JsonProperty("create_time")
    private Date createTime;

}
