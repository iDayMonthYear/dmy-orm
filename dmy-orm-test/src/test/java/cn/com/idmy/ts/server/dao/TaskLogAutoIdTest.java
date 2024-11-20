package cn.com.idmy.ts.server.dao;

import cn.com.idmy.ts.server.model.entity.TaskLog;
import cn.com.idmy.ts.server.model.enums.TaskStatus;
import cn.com.idmy.ts.server.model.enums.TriggerType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class TaskLogAutoIdTest {

    @Autowired
    private TaskLogDao taskLogDao;

    @Test
    void testAutoIncrementId() {
        // 创建实体，不设置ID
        TaskLog taskLog = createTaskLog();
        assertNull(taskLog.getId(), "ID should be null before insert");
        
        // 执行插入
        int result = taskLogDao.insert(taskLog);
        assertEquals(1, result);
        
        // 验证ID已被设置
        assertNotNull(taskLog.getId(), "ID should be set after insert");
        assertTrue(taskLog.getId() > 0, "ID should be positive");
        
        // 验证可以通过ID查询
        TaskLog found = taskLogDao.get(taskLog.getId());
        assertNotNull(found);
        assertEquals(taskLog.getId(), found.getId());
    }

    @Test
    void testBatchInsertWithAutoIncrementId() {
        // 创建多个实体
        List<TaskLog> logs = Arrays.asList(
            createTaskLog(),
            createTaskLog(),
            createTaskLog()
        );
        
        // 验证所有ID都为null
        logs.forEach(log -> assertNull(log.getId(), "ID should be null before insert"));
        
        // 执行批量插入
        int result = taskLogDao.inserts(logs);
        assertEquals(3, result);
        
        // 验证所有ID都已被设置，且是递增的
        logs.forEach(log -> assertNotNull(log.getId(), "ID should be set after insert"));
        
        // 验证ID是递增的
        for (int i = 1; i < logs.size(); i++) {
            assertTrue(logs.get(i).getId() > logs.get(i-1).getId(), 
                "IDs should be auto-incrementing");
        }
        
        // 验证所有记录都可以查询
        logs.forEach(log -> {
            TaskLog found = taskLogDao.get(log.getId());
            assertNotNull(found);
            assertEquals(log.getId(), found.getId());
        });
    }

    @Test
    void testIdGenerationOrder() {
        // 测试ID生成的顺序性
        TaskLog log1 = createTaskLog();
        TaskLog log2 = createTaskLog();
        
        taskLogDao.insert(log1);
        taskLogDao.insert(log2);
        
        assertTrue(log2.getId() > log1.getId(), 
            "Second insert should have greater ID");
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