package cn.com.idmy.ts.server.dao;

import cn.com.idmy.orm.core.UpdateChain;
import cn.com.idmy.ts.server.model.entity.TaskLog;
import cn.com.idmy.ts.server.model.enums.TaskStatus;
import cn.com.idmy.ts.server.model.enums.TriggerType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Transactional
class TaskLogUpdateTest {

    @Autowired
    private TaskLogDao taskLogDao;

    @Test
    void testUpdateSingleField() {
        TaskLog taskLog = createAndInsertTaskLog();
        
        int result = taskLogDao.update(
            UpdateChain.of(taskLogDao)
                .set(TaskLog::getStatus, TaskStatus.SUCCESS)
                .eq(TaskLog::getId, taskLog.getId())
        );
        
        assertEquals(1, result);
        
        TaskLog updated = taskLogDao.get(taskLog.getId());
        assertEquals(TaskStatus.SUCCESS, updated.getStatus());
    }

    @Test
    void testUpdateMultipleFields() {
        TaskLog taskLog = createAndInsertTaskLog();
        LocalDateTime now = LocalDateTime.now();
        
        int result = taskLogDao.update(
            UpdateChain.of(taskLogDao)
                .set(TaskLog::getStatus, TaskStatus.SUCCESS)
                .set(TaskLog::getCallbackAt, now)
                .set(TaskLog::getContent, "Updated content")
                .eq(TaskLog::getId, taskLog.getId())
        );
        
        assertEquals(1, result);
        
        TaskLog updated = taskLogDao.get(taskLog.getId());
        assertEquals(TaskStatus.SUCCESS, updated.getStatus());
        assertEquals(now, updated.getCallbackAt());
        assertEquals("Updated content", updated.getContent());
    }

    @Test
    void testUpdateWithComplexConditions() {
        TaskLog taskLog = createAndInsertTaskLog();
        
        int result = taskLogDao.update(
            UpdateChain.of(taskLogDao)
                .set(TaskLog::getStatus, TaskStatus.SUCCESS)
                .eq(TaskLog::getTaskId, taskLog.getTaskId())
                .eq(TaskLog::getStatus, TaskStatus.WAIT_RESPONSE)
                .eq(TaskLog::getTriggerType, TriggerType.MANUAL)
        );
        
        assertEquals(1, result);
    }

    @Test
    void testUpdateNonExistentRecord() {
        int result = taskLogDao.update(
            UpdateChain.of(taskLogDao)
                .set(TaskLog::getStatus, TaskStatus.SUCCESS)
                .eq(TaskLog::getId, 999999L)
        );
        
        assertEquals(0, result);
    }

    @Test
    void testUpdateWithNullValues() {
        TaskLog taskLog = createAndInsertTaskLog();
        
        int result = taskLogDao.update(
            UpdateChain.of(taskLogDao)
                .set(TaskLog::getAddress, null)
                .set(TaskLog::getContent, null)
                .eq(TaskLog::getId, taskLog.getId())
        );
        
        assertEquals(1, result);
        
        TaskLog updated = taskLogDao.get(taskLog.getId());
        assertNull(updated.getAddress());
        assertNull(updated.getContent());
    }

    private TaskLog createAndInsertTaskLog() {
        TaskLog taskLog = TaskLog.builder()
            .taskId(1)
            .status(TaskStatus.WAIT_RESPONSE)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .deathAt(LocalDateTime.now().plusHours(1))
            .retry(0)
            .timeout(LocalDateTime.now().plusMinutes(30))
            .triggerType(TriggerType.MANUAL)
            .build();
            
        taskLogDao.insert(taskLog);
        return taskLog;
    }
} 