package com.example.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import io.github.simple.jdbc.processor.SimpleJdbcRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class PhotoRepository implements SimpleJdbcRepository<Photo, Photo, PhotoExample> {

    private final DynamoDBTableMapper<Photo, String, String> dynamoDBMapper;

    public PhotoRepository(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper.newTableMapper(Photo.class);
    }

    @Override
    public Photo selectByPrimaryKey(Photo t) {
        return dynamoDBMapper.load(t.getUserId(), t.getSortKey());
    }

    @Override
    public Photo selectByPrimaryKeyForUpdate(Photo t) {
        return selectByPrimaryKey(t);
    }

    @Override
    public List<Photo> selectByPrimaryKeys(List<Photo> ts) {
        return dynamoDBMapper.batchLoad(ts);
    }

    @Override
    public List<Photo> selectByExample(PhotoExample query) {
        DynamoDBQueryExpression<Photo> queryRequest = query.buildQueryRequest();
        return dynamoDBMapper.query(queryRequest);
    }

    @Override
    public void consumeByExample(PhotoExample query, Consumer<Photo> consumer) {
        while (true) {
            DynamoDBQueryExpression<Photo> request = query.buildQueryRequest();
            request.setLimit(1000);
            QueryResultPage<Photo> photoQueryResultPage = dynamoDBMapper.queryPage(request);
            for (Photo result : photoQueryResultPage.getResults()) {
                consumer.accept(result);
            }
            if (photoQueryResultPage.getLastEvaluatedKey() == null) {
                break;
            }
            request.setExclusiveStartKey(photoQueryResultPage.getLastEvaluatedKey());
        }
    }

    @Override
    public Photo selectOne(PhotoExample query) {
        List<Photo> ts = selectByExample(query);
        return ts.isEmpty() ? null : ts.get(0);
    }

    @Override
    public List<Photo> selectAll() {
        DynamoDBScanExpression expression = new DynamoDBScanExpression();
        return dynamoDBMapper.scan(expression);
    }

    @Override
    public void insert(Photo t) {
        dynamoDBMapper.save(t);
    }

    @Override
    public void insertSelective(Photo t) {
        dynamoDBMapper.save(t);
    }

    @Override
    public void upsertSelective(Photo t) {
        dynamoDBMapper.save(t);
    }

    @Override
    public void insertBatch(List<Photo> ts) {
        dynamoDBMapper.batchSave(ts);
    }

    @Override
    public void insertIgnoreBatch(List<Photo> ts) {
        dynamoDBMapper.batchSave(ts);
    }

    @Override
    public long countByExample(PhotoExample query) {
        return dynamoDBMapper.count(query.buildQueryRequest());
    }

    @Override
    public int updateByPrimaryKeySelective(Photo t) {
        dynamoDBMapper.save(t);
        return 1;
    }

    @Override
    public int updateByPrimaryKey(Photo photo) {
        dynamoDBMapper.save(photo);
        return 1;
    }

    @Override
    public int updateByExampleSelective(Photo t, PhotoExample query) {
        List<Photo> ts = selectByExample(query);
        consumeByExample(query, ts::add);
        for (Photo old : ts) {
            if (t.getUserId() != null) {
                old.setUserId(t.getUserId());
            }
            if (t.getSortKey() != null) {
                old.setSortKey(t.getSortKey());
            }
            if (t.getSecondIndexHashKey() != null) {
                old.setSecondIndexHashKey(t.getSecondIndexHashKey());
            }
            if (t.getSecondIndexRangeKey() != null) {
                old.setSecondIndexRangeKey(t.getSecondIndexRangeKey());
            }
            if (t.getPhotoUrl() != null) {
                old.setPhotoUrl(t.getPhotoUrl());
            }
        }
        dynamoDBMapper.batchSave(ts);
        return ts.size();
    }

    @Override
    public int updateByExample(Photo t, PhotoExample query) {
        return updateByExampleSelective(t, query);
    }

    @Override
    public int deleteByPrimaryKey(Photo t) {
        dynamoDBMapper.delete(t);
        return 1;
    }

    @Override
    public int deleteByPrimaryKeys(List<Photo> photos) {
        List<DynamoDBMapper.FailedBatch> failedBatches = dynamoDBMapper.batchDelete(photos);
        return photos.size() - failedBatches.size();
    }

    @Override
    public int deleteByExample(PhotoExample query) {
        List<Photo> photos = selectByExample(query);
        return deleteByPrimaryKeys(photos);
    }

    @Override
    public boolean existsById(Photo t) {
        return selectByPrimaryKey(t) != null;
    }

    @Override
    public boolean existsByExample(PhotoExample query) {
        return selectOne(query) != null;
    }

    @Override
    public List<Photo> selectByPrimaryKeysWithSorted(List<Photo> ts) {
        return selectByPrimaryKeys(ts);
    }

    @Override
    public Map<Photo, Photo> mapById(List<Photo> ts) {
        List<Photo> ps = selectByPrimaryKeys(ts);
        Map<Photo, Photo> result = new HashMap<>();
        for (Photo p : ps) {
            result.put(p, p);
        }
        return result;
    }
}
