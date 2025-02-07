package cn.com.idmy.orm.mybatis;

import cn.com.idmy.base.model.IEnum;
import cn.com.idmy.orm.annotation.WatchEnum.Action;
import cn.com.idmy.orm.annotation.WatchEnum.Timing;
import cn.com.idmy.orm.listener.EnumWatchEvent;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.ReflectionUtils;

import java.util.Properties;

@RequiredArgsConstructor
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
class EnumWatchInterceptor implements Interceptor {
    protected final ApplicationEventPublisher eventPublisher;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        var args = invocation.getArgs();
        var ms = (MappedStatement) args[0];
        var action = getSqlAction(ms);
        if (action == null) {
            return invocation.proceed();
        }
        var parameter = args[1];
        checkEnumFields(parameter, action, Timing.BEFORE);
        var result = invocation.proceed();
        checkEnumFields(parameter, action, Timing.AFTER);
        return result;
    }

    @Nullable
    private Action getSqlAction(MappedStatement ms) {
        String id = ms.getId();
        if (id.endsWith(".insert")) {
            return Action.INSERT;
        } else if (id.endsWith(".update")) {
            return Action.UPDATE;
        } else if (id.endsWith(".delete")) {
            return Action.DELETE;
        } else {
            return null;
        }
    }

    private void checkEnumFields(@Nullable Object entity, Action action, Timing timing) {
        if (entity == null) {
            return;
        }
        // 遍历所有字段
        ReflectionUtils.doWithFields(entity.getClass(), field -> {
            field.setAccessible(true);
            Object value = field.get(entity);
            if (value instanceof IEnum) {
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