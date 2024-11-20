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
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Transactional
class TaskLogDaoAdvancedTest {

    @Autowired
    private TaskLogDao taskLogDao;
    
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        
        // 准备测试数据
        List<TaskLog> logs = Arrays.asList(
            createTaskLog(1, TaskStatus.WAIT_RESPONSE, TriggerType.MANUAL),
            createTaskLog(1, TaskStatus.SUCCESS, TriggerType.MANUAL),
            createTaskLog(2, TaskStatus.REQUEST_ERROR, TriggerType.CRON),
            createTaskLog(2, TaskStatus.SUCCESS, TriggerType.RETRY),
            createTaskLog(3, TaskStatus.WAIT_RESPONSE, TriggerType.API)
        );
        
        taskLogDao.inserts(logs);
    }

    @Test
    void testGroupByStatus() {
        // 按状态分组统计
        List<Map<String, Object>> result = taskLogDao.find(
            SelectChain.of(taskLogDao)
                .select(() -> SqlFn.count())
                .select(TaskLog::getStatus)
                .groupBy(TaskLog::getStatus)
        );
        
        assertFalse(result.isEmpty());
        assertEquals(3, result.size()); // 应该有3种不同的状态
    }

    @Test
    void testTimeRangeQuery() {
        // 测试时间范围查询
        List<TaskLog> logs = taskLogDao.find(
            SelectChain.of(taskLogDao)
                .ge(TaskLog::getCreatedAt, now.minusHours(1))
                .le(TaskLog::getCreatedAt, now.plusHours(1))
        );
        
        assertEquals(5, logs.size());
    }

    @Test
    void testMultiConditionQuery() {
        // 测试多条件组合查询
        List<TaskLog> logs = taskLogDao.find(
            SelectChain.of(taskLogDao)
                .eq(TaskLog::getTaskId, 1)
                .in(TaskLog::getStatus, Arrays.asList(TaskStatus.WAIT_RESPONSE, TaskStatus.SUCCESS))
                .eq(TaskLog::getTriggerType, TriggerType.MANUAL)
        );
        
        assertEquals(2, logs.size());
    }

    @Test
    void testDistinctQuery() {
        // 测试去重查询
        List<TaskLog> logs = taskLogDao.find(
            SelectChain.of(taskLogDao)
                .distinct()
                .select(TaskLog::getTaskId)
        );
        
        assertEquals(3, logs.size()); // 应该只有3个不同的taskId
    }

    @Test
    void testStatusTransitionAnalysis() {
        // 分析任务状态转换
        Map<Integer, List<TaskStatus>> statusTransitions = taskLogDao.find(
            SelectChain.of(taskLogDao)
                .select(TaskLog::getTaskId, TaskLog::getStatus)
                .orderBy(TaskLog::getCreatedAt, true)
        ).stream().collect(Collectors.groupingBy(
            TaskLog::getTaskId,
            Collectors.mapping(TaskLog::getStatus, Collectors.toList())
        ));
        
        // 验证taskId=1的状态转换
        List<TaskStatus> transitions = statusTransitions.get(1);
        assertEquals(2, transitions.size());
        assertEquals(TaskStatus.WAIT_RESPONSE, transitions.get(0));
        assertEquals(TaskStatus.SUCCESS, transitions.get(1));
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