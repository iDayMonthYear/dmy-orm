package cn.com.idmy.orm.mybatis;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.dromara.hutool.core.collection.CollUtil;

import java.sql.Statement;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class EntitiesIdGenerator implements KeyGenerator {
    private final KeyGenerator keyGenerator;

    @Override
    @SuppressWarnings({"unchecked"})
    public void processBefore(Executor executor, MappedStatement ms, Statement st, Object parameter) {
        var params = (Map<String, Object>) parameter;
        var entities = (List<Object>) params.get(MybatisConsts.ENTITIES);
        if (CollUtil.isNotEmpty(entities)) {
            for (Object entity : entities) {
                params.put(MybatisConsts.ENTITY, entity);
                params.put(MybatisConsts.ENTITY_CLASS, entity.getClass());
                keyGenerator.processBefore(executor, ms, st, parameter);
            }
        }
    }

    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
    }
}
