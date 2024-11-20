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
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class TaskLogBasicTest {

    @Autowired
    private TaskLogDao taskLogDao;

    @Test
    void testInsertWithAllFields() {
        TaskLog taskLog = TaskLog.builder()
            .taskId(1)
            .address("http://localhost:8080")
            .status(TaskStatus.WAIT_RESPONSE)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .deathAt(LocalDateTime.now().plusHours(1))
            .callbackAt(null)
            .retry(0)
            .timeout(LocalDateTime.now().plusMinutes(30))
            .content("Test content")
            .triggerType(TriggerType.MANUAL)
            .build();

        int result = taskLogDao.insert(taskLog);
        assertEquals(1, result);
        assertNotNull(taskLog.getId());
    }

    @Test
    void testInsertWithNullableFields() {
        TaskLog taskLog = TaskLog.builder()
            .taskId(1)
            .address(null)  // 测试可空字段
            .status(TaskStatus.WAIT_RESPONSE)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .deathAt(LocalDateTime.now().plusHours(1))
            .callbackAt(null)  // 测试可空字段
            .retry(0)
            .timeout(LocalDateTime.now().plusMinutes(30))
            .content(null)  // 测试可空字段
            .triggerType(TriggerType.MANUAL)
            .build();

        int result = taskLogDao.insert(taskLog);
        assertEquals(1, result);
        
        TaskLog saved = taskLogDao.get(taskLog.getId());
        assertNull(saved.getAddress());
        assertNull(saved.getCallbackAt());
        assertNull(saved.getContent());
    }

    @Test
    void testBatchInsert() {
        List<TaskLog> logs = Arrays.asList(
            createSampleTaskLog(1, TaskStatus.WAIT_RESPONSE),
            createSampleTaskLog(2, TaskStatus.SUCCESS)
        );

        int result = taskLogDao.inserts(logs);
        assertEquals(2, result);
        logs.forEach(log -> assertNotNull(log.getId()));
    }

    @Test
    void testBatchInsertWithEmptyList() {
        List<TaskLog> logs = List.of();
        assertThrows(RuntimeException.class, () -> taskLogDao.inserts(logs));
    }

    @Test
    void testGetById() {
        TaskLog taskLog = createAndInsertTaskLog();
        
        TaskLog found = taskLogDao.get(taskLog.getId());
        assertNotNull(found);
        assertEquals(taskLog.getTaskId(), found.getTaskId());
        assertEquals(taskLog.getStatus(), found.getStatus());
        assertEquals(taskLog.getTriggerType(), found.getTriggerType());
    }

    @Test
    void testGetByNonExistentId() {
        TaskLog found = taskLogDao.get(999999L);
        assertNull(found);
    }

    @Test
    void testDeleteById() {
        TaskLog taskLog = createAndInsertTaskLog();
        
        int result = taskLogDao.delete(taskLog.getId());
        assertEquals(1, result);
        
        TaskLog deleted = taskLogDao.get(taskLog.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteByNonExistentId() {
        int result = taskLogDao.delete(999999L);
        assertEquals(0, result);
    }

    @Test
    void testDeleteByIds() {
        List<TaskLog> logs = Arrays.asList(
            createSampleTaskLog(1, TaskStatus.WAIT_RESPONSE),
            createSampleTaskLog(2, TaskStatus.SUCCESS)
        );
        taskLogDao.inserts(logs);
        
        List<Long> ids = logs.stream().map(TaskLog::getId).toList();
        int result = taskLogDao.delete(ids);
        assertEquals(2, result);
        
        ids.forEach(id -> assertNull(taskLogDao.get(id)));
    }

    private TaskLog createSampleTaskLog(int taskId, TaskStatus status) {
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

    private TaskLog createAndInsertTaskLog() {
        TaskLog taskLog = createSampleTaskLog(1, TaskStatus.WAIT_RESPONSE);
        taskLogDao.insert(taskLog);
        return taskLog;
    }
} 