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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class TaskLogInsertWithIdTest {

    @Autowired
    private TaskLogDao taskLogDao;

    @Test
    void testInsertWithNewId() {
        // 创建实体并设置一个不存在的ID
        TaskLog taskLog = createTaskLog();
        taskLog.setId(999L);
        
        int result = taskLogDao.insert(taskLog);
        assertEquals(1, result);
        assertEquals(999L, taskLog.getId());
        
        // 验证可以通过ID查询
        TaskLog found = taskLogDao.get(999L);
        assertNotNull(found);
        assertEquals(999L, found.getId());
    }

    @Test
    void testInsertWithExistingId() {
        // 先插入一条记录
        TaskLog taskLog1 = createTaskLog();
        taskLogDao.insert(taskLog1);
        assertNotNull(taskLog1.getId());
        
        // 尝试使用已存在的ID插入
        TaskLog taskLog2 = createTaskLog();
        taskLog2.setId(taskLog1.getId());
        
        int result = taskLogDao.insert(taskLog2);
        assertEquals(1, result);
        assertNotNull(taskLog2.getId());
        assertNotEquals(taskLog1.getId(), taskLog2.getId());
    }

    @Test
    void testBatchInsertWithMixedIds() {
        // 创建一条记录获取已存在的ID
        TaskLog existing = createTaskLog();
        taskLogDao.insert(existing);
        
        // 创建测试数据：一个新ID，一个已存在的ID，一个null ID
        List<TaskLog> logs = Arrays.asList(
            createTaskLog().setId(999L),
            createTaskLog().setId(existing.getId()),
            createTaskLog()
        );
        
        int result = taskLogDao.inserts(logs);
        assertEquals(3, result);
        
        // 验证结果
        logs.forEach(log -> {
            assertNotNull(log.getId());
            TaskLog found = taskLogDao.get(log.getId());
            assertNotNull(found);
        });
        
        // 验证第二条记录的ID被重新生成
        assertNotEquals(existing.getId(), logs.get(1).getId());
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