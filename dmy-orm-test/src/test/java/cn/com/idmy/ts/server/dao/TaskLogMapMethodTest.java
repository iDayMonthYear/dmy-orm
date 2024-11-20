package cn.com.idmy.ts.server.dao;

import cn.com.idmy.ts.server.model.entity.TaskLog;
import cn.com.idmy.ts.server.model.enums.TaskStatus;
import cn.com.idmy.ts.server.model.enums.TriggerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class TaskLogMapMethodTest {

    @Autowired
    private TaskLogDao taskLogDao;

    private TaskLog taskLog1;
    private TaskLog taskLog2;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        taskLog1 = createTaskLog(1, TaskStatus.WAIT_RESPONSE);
        taskLog2 = createTaskLog(2, TaskStatus.SUCCESS);
        taskLogDao.inserts(Arrays.asList(taskLog1, taskLog2));
    }

    @Test
    void testMapWithNoIds() {
        // 测试空参数
        Map<Long, TaskLog> result = taskLogDao.map();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testMapWithNullIds() {
        // 测试null参数
        Long[] ids = null;
        Map<Long, TaskLog> result = taskLogDao.map(ids);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testMapWithSingleId() {
        // 测试单个ID
        Map<Long, TaskLog> result = taskLogDao.map(taskLog1.getId());
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(taskLog1.getId()));
        assertEquals(taskLog1.getTaskId(), result.get(taskLog1.getId()).getTaskId());
    }

    @Test
    void testMapWithMultipleIds() {
        // 测试多个ID
        Map<Long, TaskLog> result = taskLogDao.map(taskLog1.getId(), taskLog2.getId());
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey(taskLog1.getId()));
        assertTrue(result.containsKey(taskLog2.getId()));
        assertEquals(TaskStatus.WAIT_RESPONSE, result.get(taskLog1.getId()).getStatus());
        assertEquals(TaskStatus.SUCCESS, result.get(taskLog2.getId()).getStatus());
    }

    @Test
    void testMapWithNonExistentIds() {
        // 测试不存在的ID
        Map<Long, TaskLog> result = taskLogDao.map(999L, 1000L);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testMapWithMixedExistingAndNonExistingIds() {
        // 测试混合存在和不存在的ID
        Map<Long, TaskLog> result = taskLogDao.map(taskLog1.getId(), 999L);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(taskLog1.getId()));
        assertFalse(result.containsKey(999L));
    }

    @Test
    void testMapWithDuplicateIds() {
        // 测试重复的ID
        Map<Long, TaskLog> result = taskLogDao.map(
            taskLog1.getId(), 
            taskLog1.getId(), 
            taskLog2.getId()
        );
        
        assertNotNull(result);
        assertEquals(2, result.size()); // 应该只有两个元素，重复的ID只保留一个
        assertTrue(result.containsKey(taskLog1.getId()));
        assertTrue(result.containsKey(taskLog2.getId()));
    }

    private TaskLog createTaskLog(int taskId, TaskStatus status) {
        return TaskLog.builder()
            .taskId(taskId)
            .status(status)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .deathAt(LocalDateTime.now().plusHours(1))
            .retry(0)
            .timeout(LocalDateTime.now().plusMinutes(30))
            .triggerType(TriggerType.MANUAL)
            .build();
    }
} 