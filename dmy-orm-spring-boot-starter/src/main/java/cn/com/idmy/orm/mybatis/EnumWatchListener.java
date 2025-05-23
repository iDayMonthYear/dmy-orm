package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.annotation.WatchEnum;
import cn.com.idmy.orm.annotation.WatchEnum.Action;
import cn.com.idmy.orm.annotation.WatchEnum.Timing;
import cn.com.idmy.orm.listener.EnumWatchEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

class EnumWatchListener {
    private final Map<WatchKey, List<WatchMethod>> watchMethods = new ConcurrentHashMap<>();

    public EnumWatchListener(ApplicationContext ctx) {
        // 扫描所有带@WatchEnum注解的方法
        scanWatchMethods(ctx);
    }

    private void scanWatchMethods(ApplicationContext ctx) {
        ctx.getBeansOfType(Object.class).forEach((name, bean) -> {
            ReflectionUtils.doWithMethods(bean.getClass(), method -> {
                WatchEnum watchEnum = AnnotationUtils.findAnnotation(method, WatchEnum.class);
                if (watchEnum != null) {
                    WatchKey key = new WatchKey(
                            watchEnum.entity(),
                            watchEnum.value(),
                            watchEnum.action(),
                            watchEnum.timing()
                    );
                    watchMethods.computeIfAbsent(key, k -> new ArrayList<>()).add(new WatchMethod(bean, method));
                }
            });
        });
    }

    @EventListener
    public void onEnumWatchEvent(EnumWatchEvent event) {
        WatchKey key = new WatchKey(
                event.entityType(),
                event.enumClass(),
                event.action(),
                event.timing()
        );

        List<WatchMethod> methods = watchMethods.get(key);
        if (methods != null) {
            for (WatchMethod method : methods) {
                try {
                    method.method().invoke(method.bean(), event.entity());
                } catch (Exception e) {
                    throw new OrmException("Failed to invoke watch method", e);
                }
            }
        }
    }

    private record WatchKey(Class<?> entityType, Class<?> enumType, Action action, Timing timing) {
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            WatchKey watchKey = (WatchKey) o;
            return Objects.equals(entityType, watchKey.entityType) &&
                    Objects.equals(enumType, watchKey.enumType) &&
                    action == watchKey.action &&
                    timing == watchKey.timing;
        }
    }

    private record WatchMethod(Object bean, Method method) {
    }
} 