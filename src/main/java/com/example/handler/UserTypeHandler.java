package com.example.handler;

import com.example.domain.User;
import com.example.domain.UserSimpleJdbcDefaultTypeHandler;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 自定义类型处理器.
 */
@Component
public class UserTypeHandler extends UserSimpleJdbcDefaultTypeHandler {

    /**
     * 编码用户名列表.
     *
     * @param values 用户名列表
     * @return 编码后的用户名列表
     */
    @Override
    public List<String> encodeUsernameList(List<String> values) {
        return values.stream().map(this::encodeUsername).collect(Collectors.toList());
    }

    /**
     * 编码用户名.
     *
     * @param value 用户名
     * @return 编码后的用户名
     */
    @Override
    public String encodeUsername(String value) {
        if (value == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(value.getBytes());
    }

    /**
     * 解码用户名.
     *
     * @param resultSet  结果集
     * @param t          用户对象
     * @param column     列名
     * @param targetType 目标类型
     * @throws SQLException 数据库异常
     */
    @Override
    public void decodeUsername(ResultSet resultSet, User t, String column, int index, Class<String> targetType) throws SQLException {
        String value = resultSet.getObject(column, String.class);
        if (value == null) {
            return;
        }
        t.setUsername(new String(Base64.getDecoder().decode(value.getBytes())));
    }

    @Override
    public Object encodeTags(List<String> value) {
        return String.join(",", value);
    }

    @Override
    public List<Object> encodeTagsList(List<List<String>> values) {
        return values.stream().map(this::encodeTags).collect(Collectors.toList());
    }

    @Override
    public void decodeTags(ResultSet resultSet, User t, String column, int index) throws SQLException {
        String tagList = resultSet.getString(column);
        if (tagList == null) {
            return;
        }
        t.setTags(Arrays.asList(tagList.split(",")));
    }

    /**
     * 自定义主键生成策略.
     */
    @Override
    public void generatePrimaryKey(User t) {
    }

    /**
     * 批量自定义主键生成策略.
     */
    @Override
    public void batchGeneratePrimaryKey(List<User> ts) {
    }

}
