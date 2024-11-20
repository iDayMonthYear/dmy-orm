package cn.com.idmy.ts.server.service;

import cn.com.idmy.orm.annotation.WatchEnum;
import cn.com.idmy.orm.annotation.WatchEnum.WatchAction;
import cn.com.idmy.orm.annotation.WatchEnum.WatchTiming;
import cn.com.idmy.orm.core.UpdateChain;
import cn.com.idmy.ts.server.dao.TaskLogDao;
import cn.com.idmy.ts.server.model.entity.TaskLog;
import cn.com.idmy.ts.server.model.enums.TaskStatus;
import cn.com.idmy.ts.server.model.enums.TriggerType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
@Transactional
class TaskLogServiceTest {

    @Autowired
    private TaskLogDao taskLogDao;
    
    @Autowired
    private TaskLogService taskLogService;
    
    private final AtomicBoolean watchCalled = new AtomicBoolean(false);

    @Test
    void testEnumWatch() {
        // 创建任务日志
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
        
        // 更新状态，触发枚举监听
        taskLogDao.update(
            UpdateChain.of(taskLogDao)
                .set(TaskLog::getStatus, TaskStatus.SUCCESS)
                .eq(TaskLog::getId, taskLog.getId())
        );
        
        // 验证监听器被调用
        assertTrue(watchCalled.get(), "Enum watch should have been called");
    }
}

@Slf4j
@Service
class TaskLogService {
    
    @WatchEnum(
        entity = TaskLog.class,
        value = TaskStatus.class,
        action = WatchAction.UPDATE,
        timing = WatchTiming.AFTER
    )
    public void onTaskStatusChanged(TaskLog taskLog) {
        log.info("Task status changed to: {}", taskLog.getStatus());
        // 在实际应用中，这里可以处理任务状态变更后的业务逻辑
    }
} 