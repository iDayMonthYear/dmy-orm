package cn.com.idmy.ts.server.dao;

import cn.com.idmy.orm.core.SelectChain;
import cn.com.idmy.orm.core.SqlFn;
import cn.com.idmy.orm.core.UpdateChain;
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

@SpringBootTest
@Transactional
class TaskLogDaoTest {

    @Autowired
    private TaskLogDao taskLogDao;

    @Test
    void testInsert() {
        // 创建测试数据
        TaskLog taskLog = createSampleTaskLog();
        
        // 测试插入
        int result = taskLogDao.insert(taskLog);
        assertEquals(1, result);
        assertNotNull(taskLog.getId());
    }

    @Test
    void testBatchInsert() {
        // 创建测试数据
        List<TaskLog> logs = Arrays.asList(
            createSampleTaskLog(),
            createSampleTaskLog()
        );
        
        // 测试批量插入
        int result = taskLogDao.inserts(logs);
        assertEquals(2, result);
        logs.forEach(log -> assertNotNull(log.getId()));
    }

    @Test
    void testSelect() {
        // 插入测试数据
        TaskLog taskLog = createSampleTaskLog();
        taskLogDao.insert(taskLog);

        // 测试单个查询
        TaskLog found = taskLogDao.get(taskLog.getId());
        assertNotNull(found);
        assertEquals(taskLog.getTaskId(), found.getTaskId());
        assertEquals(taskLog.getStatus(), found.getStatus());

        // 测试条件查询
        List<TaskLog> logs = taskLogDao.find(
            SelectChain.of(taskLogDao)
                .eq(TaskLog::getTaskId, taskLog.getTaskId())
                .eq(TaskLog::getStatus, TaskStatus.WAIT_RESPONSE)
        );
        assertEquals(1, logs.size());
        
        // 测试聚合查询
        long count = taskLogDao.count(
            SelectChain.of(taskLogDao)
                .eq(TaskLog::getStatus, TaskStatus.WAIT_RESPONSE)
        );
        assertEquals(1, count);
    }

    @Test
    void testUpdate() {
        // 插入测试数据
        TaskLog taskLog = createSampleTaskLog();
        taskLogDao.insert(taskLog);

        // 测试更新
        int result = taskLogDao.update(
            UpdateChain.of(taskLogDao)
                .set(TaskLog::getStatus, TaskStatus.SUCCESS)
                .set(TaskLog::getCallbackAt, LocalDateTime.now())
                .eq(TaskLog::getId, taskLog.getId())
        );
        assertEquals(1, result);

        // 验证更新结果
        TaskLog updated = taskLogDao.get(taskLog.getId());
        assertEquals(TaskStatus.SUCCESS, updated.getStatus());
        assertNotNull(updated.getCallbackAt());
    }

    @Test
    void testDelete() {
        // 插入测试数据
        TaskLog taskLog = createSampleTaskLog();
        taskLogDao.insert(taskLog);

        // 测试删除
        int result = taskLogDao.delete(taskLog.getId());
        assertEquals(1, result);

        // 验证删除结果
        TaskLog deleted = taskLogDao.get(taskLog.getId());
        assertNull(deleted);
    }

    @Test
    void testComplexQuery() {
        // 插入测试数据
        TaskLog log1 = createSampleTaskLog();
        TaskLog log2 = createSampleTaskLog();
        log2.setStatus(TaskStatus.SUCCESS);
        taskLogDao.inserts(Arrays.asList(log1, log2));

        // 测试复杂查询
        List<TaskLog> logs = taskLogDao.find(
            SelectChain.of(taskLogDao)
                .select(TaskLog::getId, TaskLog::getTaskId, TaskLog::getStatus)
                .eq(TaskLog::getTaskId, log1.getTaskId())
                .in(TaskLog::getStatus, Arrays.asList(TaskStatus.WAIT_RESPONSE, TaskStatus.SUCCESS))
                .orderBy(TaskLog::getCreatedAt, true)
        );
        
        assertEquals(2, logs.size());
    }

    @Test
    void testAggregateQuery() {
        // 插入测试数据
        TaskLog log1 = createSampleTaskLog();
        TaskLog log2 = createSampleTaskLog();
        log2.setStatus(TaskStatus.SUCCESS);
        taskLogDao.inserts(Arrays.asList(log1, log2));

        // 测试分组统计
        var result = taskLogDao.get(
            SelectChain.of(taskLogDao)
                .select(() -> SqlFn.count())
                .groupBy(TaskLog::getStatus)
        );
        
        assertNotNull(result);
    }

    private TaskLog createSampleTaskLog() {
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