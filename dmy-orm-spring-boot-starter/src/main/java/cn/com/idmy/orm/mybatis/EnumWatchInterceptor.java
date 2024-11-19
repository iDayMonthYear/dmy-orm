package cn.com.idmy.orm.mybatis;

import cn.com.idmy.base.model.IEnum;
import cn.com.idmy.orm.annotation.WatchEnum.WatchAction;
import cn.com.idmy.orm.annotation.WatchEnum.WatchTiming;
import cn.com.idmy.orm.listener.EnumWatchEvent;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.ReflectionUtils;

import java.util.Properties;

@RequiredArgsConstructor
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
class EnumWatchInterceptor implements Interceptor {
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1];

        // 获取SQL类型
        WatchAction action = getSqlAction(ms);
        if (action == null) {
            return invocation.proceed();
        }

        // 检查实体类中的枚举字段
        checkEnumFields(parameter, action, WatchTiming.BEFORE);

        // 执行原方法
        Object result = invocation.proceed();

        // 执行后检查
        checkEnumFields(parameter, action, WatchTiming.AFTER);
        return result;
    }

    @Nullable
    private WatchAction getSqlAction(MappedStatement ms) {
        String id = ms.getId();
        if (id.endsWith(".insert")) {
            return WatchAction.INSERT;
        } else if (id.endsWith(".update")) {
            return WatchAction.UPDATE;
        } else if (id.endsWith(".delete")) {
            return WatchAction.DELETE;
        }
        return null;
    }

    private void checkEnumFields(Object entity, WatchAction action, WatchTiming timing) {
        if (entity == null) {
            return;
        }

        // 遍历所有字段
        ReflectionUtils.doWithFields(entity.getClass(), field -> {
            field.setAccessible(true);
            Object value = field.get(entity);

            // 检查是否是IEnum类型
            if (value instanceof IEnum) {
                // 发布事件
                eventPublisher.publishEvent(new EnumWatchEvent(
                        entity.getClass(),
                        value.getClass(),
                        action,
                        timing,
                        entity
                ));
            }
        });
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
} 