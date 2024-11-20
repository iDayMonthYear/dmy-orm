package cn.com.idmy.ts.server.dao;

import cn.com.idmy.orm.core.SelectChain;
import cn.com.idmy.orm.core.SqlFn;
import cn.com.idmy.orm.core.UpdateChain;
import cn.com.idmy.ts.server.model.entity.TaskLog;
import cn.com.idmy.ts.server.model.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class TaskLogSqlFunctionTest {

    @Autowired
    private TaskLogDao taskLogDao;

    @BeforeEach
    void setUp() {
        // 插入测试数据
        taskLogDao.insert(createTaskLog(1, 0));
        taskLogDao.insert(createTaskLog(1, 1));
        taskLogDao.insert(createTaskLog(2, 2));
    }

    @Test
    void testAggregateFunctions() {
        // 测试COUNT
        Object count = taskLogDao.get(
            SelectChain.of(taskLogDao)
                .select(() -> SqlFn.count())
        );
        assertEquals(3L, ((Number)count).longValue());

        // 测试MAX
        Object maxRetry = taskLogDao.get(
            SelectChain.of(taskLogDao)
                .select(() -> SqlFn.max(TaskLog::getRetry))
        );
        assertEquals(2, ((Number)maxRetry).intValue());

        // 测试MIN
        Object minRetry = taskLogDao.get(
            SelectChain.of(taskLogDao)
                .select(() -> SqlFn.min(TaskLog::getRetry))
        );
        assertEquals(0, ((Number)minRetry).intValue());

        // 测试AVG
        Object avgRetry = taskLogDao.get(
            SelectChain.of(taskLogDao)
                .select(() -> SqlFn.avg(TaskLog::getRetry))
        );
        assertTrue(((Number)avgRetry).doubleValue() > 0);
    }

    @Test
    void testIfNullFunction() {
        // 测试IFNULL函数
        List<TaskLog> logs = taskLogDao.find(
            SelectChain.of(taskLogDao)
                .select(() -> SqlFn.ifNull(TaskLog::getContent, "default"))
        );
        
        assertFalse(logs.isEmpty());
        logs.forEach(log -> assertNotNull(log.getContent()));
    }

    @Test
    void testArithmeticOperations() {
        // 测试算术运算
        int result = taskLogDao.update(
            UpdateChain.of(taskLogDao)
                .set(TaskLog::getRetry, col -> col.plus(1))
                .eq(TaskLog::getTaskId, 1)
        );
        
        assertEquals(2, result);
        
        List<TaskLog> updated = taskLogDao.find(
            SelectChain.of(taskLogDao)
                .eq(TaskLog::getTaskId, 1)
        );
        
        updated.forEach(log -> 
            assertTrue(log.getRetry() > 0));
    }

    private TaskLog createTaskLog(int taskId, int retry) {
        return TaskLog.builder()
            .taskId(taskId)
            .status(TaskStatus.WAIT_RESPONSE)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .deathAt(LocalDateTime.now().plusHours(1))
            .retry(retry)
            .timeout(LocalDateTime.now().plusMinutes(30))
            .build();
    }
} 