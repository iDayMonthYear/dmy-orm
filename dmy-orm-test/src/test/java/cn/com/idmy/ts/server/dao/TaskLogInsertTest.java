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
class TaskLogInsertTest {

    @Autowired
    private TaskLogDao taskLogDao;

    @Test
    void testInsertWithAutoId() {
        TaskLog taskLog = createTaskLog();
        assertNull(taskLog.getId()); // ID应该为null
        
        int result = taskLogDao.insert(taskLog);
        assertEquals(1, result);
        assertNotNull(taskLog.getId()); // 自增ID应该已经设置
    }

    @Test
    void testBatchInsertWithAutoId() {
        List<TaskLog> logs = Arrays.asList(
            createTaskLog(),
            createTaskLog()
        );
        
        logs.forEach(log -> assertNull(log.getId())); // 所有ID都应该为null
        
        int result = taskLogDao.inserts(logs);
        assertEquals(2, result);
        
        logs.forEach(log -> assertNotNull(log.getId())); // 所有ID都应该已经设置
        // 确保ID是递增的
        assertTrue(logs.get(0).getId() < logs.get(1).getId());
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