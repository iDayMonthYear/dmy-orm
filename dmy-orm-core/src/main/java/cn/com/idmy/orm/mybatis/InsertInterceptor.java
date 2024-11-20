package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.core.StringSelectChain;
import cn.com.idmy.orm.util.OrmUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.dromara.hutool.core.reflect.FieldUtil;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

@Intercepts({
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class InsertInterceptor implements Interceptor {

    @Override
    @SuppressWarnings("unchecked")
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1];

        // 只处理插入操作
        if (!ms.getId().endsWith(".insert") && !ms.getId().endsWith(".inserts")) {
            return invocation.proceed();
        }

        // 获取实体类
        Class<?> entityClass = OrmUtil.getEntityClass(ms.getId());
        var idField = OrmUtil.getIdField(entityClass);
        idField.setAccessible(true);

        // 处理单个实体或批量实体
        if (parameter instanceof Map<?, ?> paramMap) {
            Object entity = paramMap.get(MybatisConsts.ENTITY);
            if (entity != null) {
                // 单个实体插入
                handleSingleEntity(invocation, entity, idField);
            } else {
                // 批量插入
                Collection<?> entities = (Collection<?>) paramMap.get(MybatisConsts.ENTITIES);
                if (entities != null) {
                    handleMultipleEntities(invocation, entities, idField);
                }
            }
        }

        return invocation.proceed();
    }

    private void handleSingleEntity(Invocation invocation, Object entity, java.lang.reflect.Field idField) throws Exception {
        Object idValue = FieldUtil.getFieldValue(entity, idField);
        if (idValue != null) {
            // 检查ID是否存在
            Executor executor = (Executor) invocation.getTarget();
            if (!exists(executor, entity.getClass(), idValue)) {
                // ID不存在，可以插入
                return;
            }
            // ID已存在，清空ID值让数据库自动生成
            FieldUtil.setFieldValue(entity, idField, null);
        }
    }

    private void handleMultipleEntities(Invocation invocation, Collection<?> entities, java.lang.reflect.Field idField) throws Exception {
        Executor executor = (Executor) invocation.getTarget();
        for (Object entity : entities) {
            Object idValue = FieldUtil.getFieldValue(entity, idField);
            if (idValue != null && !exists(executor, entity.getClass(), idValue)) {
                // ID不存在，继续使用当前ID
                continue;
            }
            // ID已存在或为null，清空ID值让数据库自动生成
            FieldUtil.setFieldValue(entity, idField, null);
        }
    }

    private boolean exists(Executor executor, Class<?> entityClass, Object id) {
        // 使用SelectChain构建查询
        var chain = StringSelectChain.of(entityClass)
            .eq(OrmUtil.getId(entityClass), id);
        
        try {
            // 执行查询
            return executor.query(
                chain.sql().left,
                chain.sql().right,
                null
            ).size() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Failed to check entity existence", e);
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
} 