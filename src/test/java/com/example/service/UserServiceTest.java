package com.example.service;

import com.example.Application;
import com.example.domain.User;
import com.example.domain.UserExample;
import com.example.enums.UserStatus;
import com.example.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@Transactional
@Slf4j
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // 测试数据常量
    private static final String TEST_USERNAME = "test_user";
    private static final String TEST_PASSWORD = "test_password";
    private static final String TEST_NICKNAME = "Test User";
    private static final String NON_EXISTENT_USERNAME = "non_existent_user";
    private static final long NON_EXISTENT_ID = 999999L;

    // 用于跟踪测试过程中创建的用户ID，以便在测试后清理
    private final List<Long> createdUserIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        log.info("Setting up test data");
        // 确保测试环境清洁
        clearTestData();
    }

    @AfterEach
    void tearDown() {
        log.info("Tearing down test data");
        // 清理测试过程中创建的数据
//        clearTestData();
    }

    private void clearTestData() {
        if (!createdUserIds.isEmpty()) {
            log.info("Cleaning up {} test users", createdUserIds.size());
            UserExample example = UserExample.create().andIdIn(createdUserIds);
            userRepository.deleteByExample(example);
            createdUserIds.clear();
        }
    }

    @Test
    void testInsertSelective() {
        log.info("Executing testInsertSelective");
        // 准备测试数据
        User user = new User();
        user.setUsername(TEST_USERNAME + "_" + UUID.randomUUID().toString().substring(0, 8));
        user.setPassword(TEST_PASSWORD);
        user.setNickname(TEST_NICKNAME);
        user.setStatus(UserStatus.NORMAL);
        Date now = new Date();
        user.setCreateTime(now);
        user.setUpdateTime(now);

        // 执行操作
        userRepository.insertSelective(user);
        createdUserIds.add(user.getId());

        // 验证结果
        assertNotNull(user.getId(), "User ID should not be null after insertion");
        User insertedUser = userRepository.selectByPrimaryKey(user.getId());
        assertNotNull(insertedUser, "Inserted user should be found");
        assertEquals(user.getUsername(), insertedUser.getUsername(), "Username should match");
        assertEquals(user.getNickname(), insertedUser.getNickname(), "Nickname should match");
        log.info("testInsertSelective passed, inserted user ID: {}", user.getId());
    }

    @Test
    void testSelectByPrimaryKey() {
        log.info("Executing testSelectByPrimaryKey");
        // 准备测试数据
        User user = createTestUser();

        // 执行操作
        User foundUser = userRepository.selectByPrimaryKey(user.getId());

        // 验证结果
        assertNotNull(foundUser, "User should be found by primary key");
        assertEquals(user.getId(), foundUser.getId(), "User ID should match");
        assertEquals(user.getUsername(), foundUser.getUsername(), "Username should match");
        log.info("testSelectByPrimaryKey passed, user: {}", foundUser);
    }

    @Test
    void testSelectByExample() {
        log.info("Executing testSelectByExample");
        // 准备测试数据
        User user = createTestUser();

        // 执行操作
        UserExample query = UserExample.create().andUsernameEqualTo(user.getUsername());
        User foundUser = userRepository.selectOne(query);

        // 验证结果
        assertNotNull(foundUser, "User should be found by example");
        assertEquals(user.getId(), foundUser.getId(), "User ID should match");
        assertEquals(user.getUsername(), foundUser.getUsername(), "Username should match");
        log.info("testSelectByExample passed, found user: {}", foundUser);
    }

    @Test
    void testUpdateByPrimaryKey() {
        log.info("Executing testUpdateByPrimaryKey");
        // 准备测试数据
        User user = createTestUser();
        String newNickname = "Updated " + TEST_NICKNAME;

        // 执行操作
        user.setNickname(newNickname);
        user.setUpdateTime(new Date());
        userRepository.updateByPrimaryKey(user);

        // 验证结果
        User updatedUser = userRepository.selectByPrimaryKey(user.getId());
        assertNotNull(updatedUser, "Updated user should be found");
        assertEquals(newNickname, updatedUser.getNickname(), "Nickname should be updated");
        log.info("testUpdateByPrimaryKey passed, updated user nickname: {}", newNickname);
    }

    @Test
    void testDeleteByPrimaryKey() {
        log.info("Executing testDeleteByPrimaryKey");
        // 准备测试数据
        User user = createTestUser();
        Long userId = user.getId();

        // 执行操作
        userRepository.deleteByPrimaryKey(userId);
        // 从跟踪列表中移除，避免重复清理
        createdUserIds.remove(userId);

        // 验证结果
        User deletedUser = userRepository.selectByPrimaryKey(userId);
        assertNull(deletedUser, "User should be deleted");
        log.info("testDeleteByPrimaryKey passed, deleted user ID: {}", userId);
    }

    @Test
    void testDeleteByExample() {
        log.info("Executing testDeleteByExample");
        // 准备测试数据
        User user1 = createTestUser();
        User user2 = createTestUser();
        String testPrefix = "test_prefix_";
        user1.setUsername(testPrefix + UUID.randomUUID().toString().substring(0, 8));
        user2.setUsername(testPrefix + UUID.randomUUID().toString().substring(0, 8));
        userRepository.updateByPrimaryKey(user1);
        userRepository.updateByPrimaryKey(user2);

        // 执行操作
        UserExample example = UserExample.create()
                .andIdIn(createdUserIds);
        int deleteCount = userRepository.deleteByExample(example);

        // 验证结果
        assertTrue(deleteCount >= 2, "At least 2 users should be deleted");
        List<User> remainingUsers = userRepository.selectByExample(example);
        assertTrue(remainingUsers.isEmpty(), "No users should remain matching the example");
        log.info("testDeleteByExample passed, deleted count: {}", deleteCount);
    }

    @Test
    void testSelectNonExistentUser() {
        log.info("Executing testSelectNonExistentUser");
        // 执行操作 - 查询不存在的用户
        User user = userRepository.selectByPrimaryKey(NON_EXISTENT_ID);

        // 验证结果
        assertNull(user, "Non-existent user should return null");
        log.info("testSelectNonExistentUser passed");
    }

    @Test
    void testCountByExample() {
        log.info("Executing testCountByExample");
        // 准备测试数据
        User user1 = createTestUser();
        User user2 = createTestUser();
        UserStatus status = UserStatus.NORMAL;

        // 执行操作
        UserExample example = UserExample.create().andStatusEqualTo(status);
        long count = userRepository.countByExample(example);

        // 验证结果
        assertTrue(count >= 2, "Count should be at least 2");
        log.info("testCountByExample passed, count: {}", count);
    }

    // 辅助方法：创建测试用户
    private User createTestUser() {
        User user = new User();
        user.setUsername(TEST_USERNAME + "_" + UUID.randomUUID().toString().substring(0, 8));
        user.setPassword(TEST_PASSWORD);
        user.setNickname(TEST_NICKNAME);
        user.setStatus(UserStatus.NORMAL);
        Date now = new Date();
        user.setCreateTime(now);
        user.setUpdateTime(now);

        userRepository.insertSelective(user);
        createdUserIds.add(user.getId());

        return user;
    }

    @Test
    public void testInsertBatch() {
        Date now = new Date();
        log.info("Executing testInsertBatch");
        User user = new User();
        user.setUsername(TEST_USERNAME + "_" + UUID.randomUUID().toString().substring(0, 8));
        user.setPassword(TEST_PASSWORD);
        user.setNickname(TEST_NICKNAME);
        user.setStatus(UserStatus.NORMAL);
        user.setCreateTime(now);
        user.setUpdateTime(now);
        user.setId(1L);
        User user2 = new User();
        user2.setUsername(TEST_USERNAME + "_" + UUID.randomUUID().toString().substring(0, 8));
        user2.setPassword(TEST_PASSWORD);
        user2.setNickname(TEST_NICKNAME);
        user2.setStatus(UserStatus.NORMAL);
        user2.setCreateTime(now);
        user2.setUpdateTime(now);
        user2.setId(2L);
        User user3 = new User();
        user3.setUsername(TEST_USERNAME + "_" + UUID.randomUUID().toString().substring(0, 8));
        user3.setPassword(TEST_PASSWORD);
        user3.setNickname(TEST_NICKNAME);
        user3.setStatus(UserStatus.NORMAL);
        user3.setCreateTime(now);
        user3.setUpdateTime(now);
        userRepository.insertBatch(Arrays.asList(user, user2, user3));
        userRepository.deleteByPrimaryKeys(Arrays.asList(user.getId(), user2.getId(), user3.getId()));
    }
}