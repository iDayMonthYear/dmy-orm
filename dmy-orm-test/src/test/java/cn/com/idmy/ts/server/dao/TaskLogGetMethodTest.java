package cn.com.idmy.ts.server.dao;

import cn.com.idmy.orm.core.SelectChain;
import cn.com.idmy.orm.core.UpdateChain;
import cn.com.idmy.ts.server.model.entity.TaskLog;
import cn.com.idmy.ts.server.model.enums.TaskStatus;
import cn.com.idmy.ts.server.model.enums.TriggerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class TaskLogGetMethodTest {

    @Autowired
    private TaskLogDao taskLogDao;

    private TaskLog taskLog1;
    private TaskLog taskLog2;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        taskLog1 = createTaskLog(1, TaskStatus.WAIT_RESPONSE);
        taskLog2 = createTaskLog(1, TaskStatus.SUCCESS);
        taskLogDao.insert(taskLog1);
        taskLogDao.insert(taskLog2);
    }

    @Test
    void testGetWithNoResult() {
        // 查询一个不存在的记录
        TaskLog result = taskLogDao.get(
            SelectChain.of(taskLogDao)
                .eq(TaskLog::getTaskId, 999)
        );
        
        assertNull(result, "Should return null when no record found");
    }

    @Test
    void testGetWithSingleResult() {
        // 查询一个存在的记录
        TaskLog result = taskLogDao.get(
            SelectChain.of(taskLogDao)
                .eq(TaskLog::getId, taskLog1.getId())
        );
        
        assertNotNull(result);
        assertEquals(taskLog1.getId(), result.getId());
    }

    @Test
    void testGetWithMultipleResults() {
        // 查询条件会返回多条记录
        assertThrows(IncorrectResultSizeDataAccessException.class, () -> 
            taskLogDao.get(
                SelectChain.of(taskLogDao)
                    .eq(TaskLog::getTaskId, 1)
            )
        );
    }

    @Test
    void testGetWithExactlyOneResult() {
        // 确保条件只返回一条记录
        TaskLog result = taskLogDao.get(
            SelectChain.of(taskLogDao)
                .eq(TaskLog::getTaskId, 1)
                .eq(TaskLog::getStatus, TaskStatus.WAIT_RESPONSE)
        );
        
        assertNotNull(result);
        assertEquals(TaskStatus.WAIT_RESPONSE, result.getStatus());
    }

    @Test
    void testGetWithNullFields() {
        // 测试包含null字段的查询
        taskLog1.setContent(null);
        taskLog1.setAddress(null);
        taskLogDao.update(
            UpdateChain.of(taskLogDao)
                .set(TaskLog::getContent, null)
                .set(TaskLog::getAddress, null)
                .eq(TaskLog::getId, taskLog1.getId())
        );
        
        TaskLog result = taskLogDao.get(
            SelectChain.of(taskLogDao)
                .eq(TaskLog::getId, taskLog1.getId())
        );
        
        assertNotNull(result);
        assertNull(result.getContent());
        assertNull(result.getAddress());
    }

    private TaskLog createTaskLog(int taskId, TaskStatus status) {
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
} 