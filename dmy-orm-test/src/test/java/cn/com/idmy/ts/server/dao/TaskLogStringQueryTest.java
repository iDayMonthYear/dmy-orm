package cn.com.idmy.ts.server.dao;

import cn.com.idmy.orm.core.StringSelectChain;
import cn.com.idmy.ts.server.model.entity.TaskLog;
import cn.com.idmy.ts.server.model.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class TaskLogStringQueryTest {

    @Autowired
    private TaskLogDao taskLogDao;

    @BeforeEach
    void setUp() {
        taskLogDao.insert(createTaskLog(1));
        taskLogDao.insert(createTaskLog(2));
    }

    @Test
    void testStringFieldSelection() {
        List<TaskLog> logs = taskLogDao.find(
            StringSelectChain.of(taskLogDao)
                .select("id", "taskId", "status")
        );
        
        assertFalse(logs.isEmpty());
        logs.forEach(log -> {
            assertNotNull(log.getId());
            assertNotNull(log.getTaskId());
            assertNotNull(log.getStatus());
            assertNull(log.getCreatedAt()); // 未选择的字段应为null
        });
    }

    @Test
    void testStringConditions() {
        List<TaskLog> logs = taskLogDao.find(
            StringSelectChain.of(taskLogDao)
                .eq("taskId", 1)
                .gt("retry", 0)
        );
        
        assertFalse(logs.isEmpty());
    }

    @Test
    void testInvalidFieldName() {
        // 测试非法字段名
        assertThrows(IllegalArgumentException.class, () -> 
            taskLogDao.find(
                StringSelectChain.of(taskLogDao)
                    .eq("invalid;field", 1)
            )
        );
    }

    private TaskLog createTaskLog(int taskId) {
        return TaskLog.builder()
            .taskId(taskId)
            .status(TaskStatus.WAIT_RESPONSE)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .deathAt(LocalDateTime.now().plusHours(1))
            .retry(1)
            .timeout(LocalDateTime.now().plusMinutes(30))
            .build();
    }
} 