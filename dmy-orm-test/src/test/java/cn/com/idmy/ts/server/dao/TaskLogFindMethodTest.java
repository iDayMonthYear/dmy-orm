package cn.com.idmy.ts.server.dao;

import cn.com.idmy.orm.core.SelectChain;
import cn.com.idmy.ts.server.model.entity.TaskLog;
import cn.com.idmy.ts.server.model.enums.TaskStatus;
import cn.com.idmy.ts.server.model.enums.TriggerType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class TaskLogFindMethodTest {

    @Autowired
    private TaskLogDao taskLogDao;

    @Test
    void testFindWithNoResult() {
        // 查询不存在的记录
        List<TaskLog> results = taskLogDao.find(
            SelectChain.of(taskLogDao)
                .eq(TaskLog::getTaskId, 999)
        );
        
        assertNotNull(results, "Should not return null");
        assertTrue(results.isEmpty(), "Should return empty list when no records found");
    }

    @Test
    void testFindWithResults() {
        // 插入测试数据
        TaskLog taskLog = createTaskLog();
        taskLogDao.insert(taskLog);
        
        List<TaskLog> results = taskLogDao.find(
            SelectChain.of(taskLogDao)
                .eq(TaskLog::getTaskId, taskLog.getTaskId())
        );
        
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }

    @Test
    void testFindWithNullFields() {
        // 测试包含null字段的查询
        TaskLog taskLog = createTaskLog();
        taskLog.setContent(null);
        taskLog.setAddress(null);
        taskLogDao.insert(taskLog);
        
        List<TaskLog> results = taskLogDao.find(
            SelectChain.of(taskLogDao)
                .eq(TaskLog::getContent, null)
                .eq(TaskLog::getAddress, null)
        );
        
        assertNotNull(results);
        assertFalse(results.isEmpty());
        results.forEach(result -> {
            assertNull(result.getContent());
            assertNull(result.getAddress());
        });
    }

    private TaskLog createTaskLog() {
        return TaskLog.builder()
            .taskId(1)
            .status(TaskStatus.WAIT_RESPONSE)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .deathAt(LocalDateTime.now().plusHours(1))
            .retry(0)
            .timeout(LocalDateTime.now().plusMinutes(30))
            .triggerType(TriggerType.MANUAL)
            .build();
    }
} 