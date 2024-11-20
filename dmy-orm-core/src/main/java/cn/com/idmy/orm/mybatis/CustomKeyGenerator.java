package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.util.OrmUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.dromara.hutool.core.reflect.FieldUtil;

import java.sql.Statement;
import java.util.Collection;
import java.util.Map;

public class CustomKeyGenerator implements KeyGenerator {

    @Override
    public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        // 插入前不需要处理
    }

    @Override
    @SuppressWarnings("unchecked")
    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        if (parameter == null) {
            return;
        }

        try {
            // 获取实体类
            Class<?> entityClass = OrmUtil.getEntityClass(ms.getId());
            if (!OrmUtil.shouldIncludeId(entityClass)) { // 只处理自增ID
                // 获取生成的主键值
                var idField = OrmUtil.getIdField(entityClass);
                idField.setAccessible(true);

                if (parameter instanceof Map<?, ?> paramMap) {
                    Object entity = paramMap.get(MybatisConsts.ENTITY);
                    if (entity != null) {
                        // 单个实体插入
                        processGeneratedKey(stmt, entity, idField);
                    } else {
                        // 批量插入
                        Collection<?> entities = (Collection<?>) paramMap.get(MybatisConsts.ENTITIES);
                        if (entities != null) {
                            processGeneratedKeys(stmt, entities, idField);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to process generated keys", e);
        }
    }

    private void processGeneratedKey(Statement stmt, Object entity, java.lang.reflect.Field idField) throws Exception {
        try (var rs = stmt.getGeneratedKeys()) {
            if (rs.next()) {
                Object id = rs.getObject(1);
                FieldUtil.setFieldValue(entity, idField, id);
            }
        }
    }

    private void processGeneratedKeys(Statement stmt, Collection<?> entities, java.lang.reflect.Field idField) throws Exception {
        try (var rs = stmt.getGeneratedKeys()) {
            for (Object entity : entities) {
                if (rs.next()) {
                    Object id = rs.getObject(1);
                    FieldUtil.setFieldValue(entity, idField, id);
                }
            }
        }
    }
} 