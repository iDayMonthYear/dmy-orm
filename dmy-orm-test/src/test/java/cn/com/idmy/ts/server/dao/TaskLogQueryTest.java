package cn.com.idmy.ts.server.dao;

import cn.com.idmy.orm.core.SelectChain;
import cn.com.idmy.orm.core.SqlFn;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class TaskLogQueryTest {

    @Autowired
    private TaskLogDao taskLogDao;

    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        List<TaskLog> logs = Arrays.asList(
            createTaskLog(1, TaskStatus.WAIT_RESPONSE, TriggerType.MANUAL),
            createTaskLog(1, TaskStatus.SUCCESS, TriggerType.MANUAL),
            createTaskLog(2, TaskStatus.REQUEST_ERROR, TriggerType.CRON),
            createTaskLog(2, TaskStatus.SUCCESS, TriggerType.RETRY)
        );
        taskLogDao.inserts(logs);
    }

    @Test
    void testSimpleConditions() {
        // 测试等于条件
        List<TaskLog> logs = taskLogDao.find(
            SelectChain.of(taskLogDao)
                .eq(TaskLog::getTaskId, 1)
        );
        assertEquals(2, logs.size());

        // 测试不等于条件
        logs = taskLogDao.find(
            SelectChain.of(taskLogDao)
                .ne(TaskLog::getStatus, TaskStatus.SUCCESS)
        );
        assertEquals(2, logs.size());

        // 测试大于条件
        logs = taskLogDao.find(
            SelectChain.of(taskLogDao)
                .gt(TaskLog::getTaskId, 1)
        );
        assertEquals(2, logs.size());

        // 测试小于等于条件
        logs = taskLogDao.find(
            SelectChain.of(taskLogDao)
                .le(TaskLog::getTaskId, 1)
        );
        assertEquals(2, logs.size());
    }

    @Test
    void testInCondition() {
        List<TaskLog> logs = taskLogDao.find(
            SelectChain.of(taskLogDao)
                .in(TaskLog::getStatus, Arrays.asList(TaskStatus.SUCCESS, TaskStatus.WAIT_RESPONSE))
        );
        assertEquals(3, logs.size());
    }

    @Test
    void testSelectSpecificFields() {
        List<TaskLog> logs = taskLogDao.find(
            SelectChain.of(taskLogDao)
                .select(TaskLog::getId, TaskLog::getTaskId, TaskLog::getStatus)
        );
        
        assertEquals(4, logs.size());
        logs.forEach(log -> {
            assertNotNull(log.getId());
            assertNotNull(log.getTaskId());
            assertNotNull(log.getStatus());
            assertNull(log.getTriggerType()); // 未选择的字段应为null
        });
    }

    @Test
    void testOrderBy() {
        List<TaskLog> logs = taskLogDao.find(
            SelectChain.of(taskLogDao)
                .orderBy(TaskLog::getTaskId, true)
                .orderBy(TaskLog::getCreatedAt, false)
        );
        
        assertEquals(4, logs.size());
        assertTrue(logs.get(0).getTaskId() <= logs.get(1).getTaskId());
    }

    @Test
    void testAggregateFunction() {
        // 测试COUNT
        TaskLog result = taskLogDao.get(
            SelectChain.of(taskLogDao)
                .select(() -> SqlFn.count())
        );
        assertEquals(4L, result.getId()); // count结果会映射到id字段

        // 测试按状态分组COUNT
        List<TaskLog> results = taskLogDao.find(
            SelectChain.of(taskLogDao)
                .select(() -> SqlFn.count())
                .select(TaskLog::getStatus)
                .groupBy(TaskLog::getStatus)
        );
        assertFalse(results.isEmpty());
    }

    @Test
    void testComplexConditions() {
        List<TaskLog> logs = taskLogDao.find(
            SelectChain.of(taskLogDao)
                .eq(TaskLog::getTaskId, 1)
                .eq(TaskLog::getTriggerType, TriggerType.MANUAL)
                .or()
                .eq(TaskLog::getStatus, TaskStatus.REQUEST_ERROR)
        );
        assertEquals(3, logs.size());
    }

    private TaskLog createTaskLog(int taskId, TaskStatus status, TriggerType triggerType) {
        return TaskLog.builder()
            .taskId(taskId)
            .status(status)
            .createdAt(now)
            .updatedAt(now)
            .deathAt(now.plusHours(1))
            .retry(0)
            .timeout(now.plusMinutes(30))
            .triggerType(triggerType)
            .build();
    }
} 