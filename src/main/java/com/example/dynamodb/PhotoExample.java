package com.example.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.*;

import java.util.*;

@SuppressWarnings("unchecked")
public class PhotoExample implements java.io.Serializable {

    private static final long serialVersionUID = 1000000L;
    private static final List<String> COLUMNS = Arrays.asList("id", "userId", "rank", "name");

    // 查询相关属性
    private String tableName = "Photo";
    private Map<String, Condition> keyConditions = new HashMap<>();
    private Map<String, Condition> filterConditions = new HashMap<>();
    private Map<String, AttributeValue> exclusiveStartKey;
    private Integer limit;
    private Integer page; // 页码
    private Integer pageSize; // 每页大小
    private String indexName;
    private Boolean scanIndexForward = true;
    private List<String> projectionExpression;
    private Map<String, String> expressionAttributeNames;
    private Map<String, AttributeValue> expressionAttributeValues;
    private String keyConditionExpression;
    private String filterExpression;

    // 更新相关属性
    private Map<String, AttributeValueUpdate> attributeUpdates = new HashMap<>();
    private String updateExpression;
    private String conditionExpression;

    public PhotoExample() {
    }

    public static PhotoExample create() {
        return new PhotoExample();
    }

    // ========== 哈希键和范围键条件 ==========

    public PhotoExample withHashKeyEqualTo(Long userId) {
        Condition condition = new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withN(userId.toString()));
        keyConditions.put("userId", condition);
        return this;
    }

    public PhotoExample withRangeKeyEqualTo(Long rank) {
        Condition condition = new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withN(rank.toString()));
        keyConditions.put("rank", condition);
        return this;
    }

    public PhotoExample withRangeKeyGreaterThan(Long rank) {
        Condition condition = new Condition()
                .withComparisonOperator(ComparisonOperator.GT)
                .withAttributeValueList(new AttributeValue().withN(rank.toString()));
        keyConditions.put("rank", condition);
        return this;
    }

    public PhotoExample withRangeKeyGreaterThanOrEqualTo(Long rank) {
        Condition condition = new Condition()
                .withComparisonOperator(ComparisonOperator.GE)
                .withAttributeValueList(new AttributeValue().withN(rank.toString()));
        keyConditions.put("rank", condition);
        return this;
    }

    public PhotoExample withRangeKeyLessThan(Long rank) {
        Condition condition = new Condition()
                .withComparisonOperator(ComparisonOperator.LT)
                .withAttributeValueList(new AttributeValue().withN(rank.toString()));
        keyConditions.put("rank", condition);
        return this;
    }

    public PhotoExample withRangeKeyLessThanOrEqualTo(Long rank) {
        Condition condition = new Condition()
                .withComparisonOperator(ComparisonOperator.LE)
                .withAttributeValueList(new AttributeValue().withN(rank.toString()));
        keyConditions.put("rank", condition);
        return this;
    }

    public PhotoExample withRangeKeyBetween(Long startRank, Long endRank) {
        Condition condition = new Condition()
                .withComparisonOperator(ComparisonOperator.BETWEEN)
                .withAttributeValueList(
                        new AttributeValue().withN(startRank.toString()),
                        new AttributeValue().withN(endRank.toString()));
        keyConditions.put("rank", condition);
        return this;
    }

    public PhotoExample withRangeKeyIn(List<Long> ranks) {
        List<AttributeValue> attributeValues = new ArrayList<>();
        for (Long rank : ranks) {
            attributeValues.add(new AttributeValue().withN(rank.toString()));
        }
        Condition condition = new Condition()
                .withComparisonOperator(ComparisonOperator.IN)
                .withAttributeValueList(attributeValues);
        keyConditions.put("rank", condition);
        return this;
    }

    // ========== 过滤条件 ==========

    public PhotoExample andNameEqualTo(String name) {
        Condition condition = new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(name));
        filterConditions.put("name", condition);
        return this;
    }

    public PhotoExample andNameContains(String name) {
        Condition condition = new Condition()
                .withComparisonOperator(ComparisonOperator.CONTAINS)
                .withAttributeValueList(new AttributeValue().withS(name));
        filterConditions.put("name", condition);
        return this;
    }

    public PhotoExample andNameBeginsWith(String prefix) {
        Condition condition = new Condition()
                .withComparisonOperator(ComparisonOperator.BEGINS_WITH)
                .withAttributeValueList(new AttributeValue().withS(prefix));
        filterConditions.put("name", condition);
        return this;
    }

    public PhotoExample andNameIn(List<String> names) {
        List<AttributeValue> attributeValues = new ArrayList<>();
        for (String name : names) {
            attributeValues.add(new AttributeValue().withS(name));
        }
        Condition condition = new Condition()
                .withComparisonOperator(ComparisonOperator.IN)
                .withAttributeValueList(attributeValues);
        filterConditions.put("name", condition);
        return this;
    }

    // ========== 高级查询选项 ==========

    public PhotoExample limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public PhotoExample indexName(String indexName) {
        this.indexName = indexName;
        return this;
    }

    public PhotoExample exclusiveStartKey(Map<String, AttributeValue> exclusiveStartKey) {
        this.exclusiveStartKey = exclusiveStartKey;
        return this;
    }

    // 更便捷的排序方法
    public PhotoExample asc() {
        this.scanIndexForward = true;
        return this;
    }

    public PhotoExample desc() {
        this.scanIndexForward = false;
        return this;
    }

    public PhotoExample columns(String... projectionExpression) {
        this.projectionExpression = Arrays.asList(projectionExpression);
        return this;
    }

    public PhotoExample columns(List<String> projectionExpression) {
        this.projectionExpression = projectionExpression;
        return this;
    }

    public PhotoExample withKeyConditionExpression(String keyConditionExpression) {
        this.keyConditionExpression = keyConditionExpression;
        return this;
    }

    public PhotoExample withFilterExpression(String filterExpression) {
        this.filterExpression = filterExpression;
        return this;
    }

    public PhotoExample withExpressionAttributeNames(Map<String, String> expressionAttributeNames) {
        this.expressionAttributeNames = expressionAttributeNames;
        return this;
    }

    public PhotoExample withExpressionAttributeValues(Map<String, AttributeValue> expressionAttributeValues) {
        this.expressionAttributeValues = expressionAttributeValues;
        return this;
    }

    // ========== 更新API ==========

    public PhotoExample withUpdateExpression(String updateExpression) {
        this.updateExpression = updateExpression;
        return this;
    }

    public PhotoExample withConditionExpression(String conditionExpression) {
        this.conditionExpression = conditionExpression;
        return this;
    }

    public PhotoExample addAttributeUpdate(String attributeName, AttributeValueUpdate attributeValueUpdate) {
        this.attributeUpdates.put(attributeName, attributeValueUpdate);
        return this;
    }

    // ========== 构建请求 ==========

    public DynamoDBQueryExpression<Photo> buildQueryRequest() {
        DynamoDBQueryExpression<Photo> queryRequest = new DynamoDBQueryExpression<Photo>()
                .withKeyConditionExpression(keyConditionExpression)
                .withQueryFilter(filterConditions)
                .withScanIndexForward(scanIndexForward);

        if (limit != null) {
            queryRequest.setLimit(limit);
        }
        if (indexName != null) {
            queryRequest.setIndexName(indexName);
        }
        if (exclusiveStartKey != null) {
            queryRequest.setExclusiveStartKey(exclusiveStartKey);
        }
        if (projectionExpression != null && !projectionExpression.isEmpty()) {
            queryRequest.setProjectionExpression(String.join(",", projectionExpression));
        }

        // 使用表达式API（如果设置了的话）
        if (keyConditionExpression != null) {
            queryRequest.setKeyConditionExpression(keyConditionExpression);
        }
        if (filterExpression != null) {
            queryRequest.setFilterExpression(filterExpression);
        }
        if (expressionAttributeNames != null) {
            queryRequest.setExpressionAttributeNames(expressionAttributeNames);
        }
        if (expressionAttributeValues != null) {
            queryRequest.setExpressionAttributeValues(expressionAttributeValues);
        }

        return queryRequest;
    }

    public ScanRequest buildScanRequest() {
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(tableName);

        if (limit != null) {
            scanRequest.setLimit(limit);
        }
        if (exclusiveStartKey != null) {
            scanRequest.setExclusiveStartKey(exclusiveStartKey);
        }
        if (filterConditions != null && !filterConditions.isEmpty()) {
            scanRequest.setScanFilter(filterConditions);
        }
        if (projectionExpression != null && !projectionExpression.isEmpty()) {
            scanRequest.setProjectionExpression(String.join(",", projectionExpression));
        }

        // 使用表达式API（如果设置了的话）
        if (filterExpression != null) {
            scanRequest.setFilterExpression(filterExpression);
        }
        if (expressionAttributeNames != null) {
            scanRequest.setExpressionAttributeNames(expressionAttributeNames);
        }
        if (expressionAttributeValues != null) {
            scanRequest.setExpressionAttributeValues(expressionAttributeValues);
        }

        return scanRequest;
    }

    // ========== Getter方法 ==========

    public String getTableName() {
        return tableName;
    }

    public Map<String, Condition> getKeyConditions() {
        return keyConditions;
    }

    public Map<String, Condition> getFilterConditions() {
        return filterConditions;
    }

    public Integer getLimit() {
        return limit;
    }

    public String getIndexName() {
        return indexName;
    }

    public Boolean getScanIndexForward() {
        return scanIndexForward;
    }

    public List<String> getProjectionExpression() {
        return projectionExpression;
    }

    public String getKeyConditionExpression() {
        return keyConditionExpression;
    }

    public String getFilterExpression() {
        return filterExpression;
    }

    public Map<String, AttributeValue> getExclusiveStartKey() {
        return exclusiveStartKey;
    }

    public Map<String, String> getExpressionAttributeNames() {
        return expressionAttributeNames;
    }

    public Map<String, AttributeValue> getExpressionAttributeValues() {
        return expressionAttributeValues;
    }

    public Map<String, AttributeValueUpdate> getAttributeUpdates() {
        return attributeUpdates;
    }

    public String getUpdateExpression() {
        return updateExpression;
    }

    public String getConditionExpression() {
        return conditionExpression;
    }

    // 分页相关的Getter方法
    public Integer getPage() {
        return page;
    }

    public Integer getPageSize() {
        return pageSize;
    }
}