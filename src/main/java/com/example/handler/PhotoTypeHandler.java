package com.example.handler;

import com.example.domain.Photo;
import com.example.domain.PhotoSimpleJdbcDefaultTypeHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;


/**
 * 自定义类型处理器.
 */
@RequiredArgsConstructor
@Component
public class PhotoTypeHandler extends PhotoSimpleJdbcDefaultTypeHandler {

    @Override
    public AttributeValue encodeMetadata(Map<String, String> value) {
        Map<String, AttributeValue> map = new HashMap<>();
        for (Map.Entry<String, String> entry : value.entrySet()) {
            map.put(entry.getKey(), AttributeValue.builder().s(entry.getValue()).build());
        }
        return AttributeValue.builder().m(map).build();
    }

    @Override
    public void decodeMetadata(Map<String, AttributeValue> m, Photo t, String column) {
        Map<String, String> map = new HashMap<>();
        AttributeValue attributeValue = m.get(column);
        if (attributeValue == null) {
            return;
        }
        for (Map.Entry<String, AttributeValue> entry : attributeValue.m().entrySet()) {
            map.put(entry.getKey(), entry.getValue().s());
        }
        t.setMetadata(map);
    }
}
