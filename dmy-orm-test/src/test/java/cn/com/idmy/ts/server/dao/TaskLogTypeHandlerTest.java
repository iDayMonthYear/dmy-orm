package cn.com.idmy.ts.server.dao;

import cn.com.idmy.orm.mybatis.CustomTypeHandlerRegistry;
import cn.com.idmy.ts.server.model.entity.TaskLog;
import cn.com.idmy.ts.server.model.enums.TaskStatus;
import cn.com.idmy.ts.server.model.enums.TriggerType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class TaskLogTypeHandlerTest {

    @Autowired
    private TaskLogDao taskLogDao;

    @Test
    void testEnumTypeHandler() {
        // 测试枚举值的存储和读取
        TaskLog taskLog = createTaskLog();
        taskLogDao.insert(taskLog);

        TaskLog found = taskLogDao.get(taskLog.getId());
        assertEquals(TaskStatus.WAIT_RESPONSE, found.getStatus());
        assertEquals(TriggerType.MANUAL, found.getTriggerType());
    }

    @Test
    void testCustomTypeHandler() {
        // 测试自定义TypeHandler注册
        Class<?> handler = CustomTypeHandlerRegistry.getHandler(TaskLog.class, "status");
        assertNotNull(handler, "Should have registered type handler for status field");
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