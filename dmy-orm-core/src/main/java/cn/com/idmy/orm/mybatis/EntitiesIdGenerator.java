package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.core.MybatisSqlProvider;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.dromara.hutool.core.collection.CollUtil;

import java.sql.Statement;
import java.util.Map;

@RequiredArgsConstructor
public class EntitiesIdGenerator implements KeyGenerator {
    private final KeyGenerator keyGenerator;

    @Override
    @SuppressWarnings({"unchecked"})
    public void processBefore(Executor executor, MappedStatement ms, Statement st, Object param) {
        var params = (Map<String, Object>) param;
        var entities = MybatisSqlProvider.findEntities(params);
        if (CollUtil.isNotEmpty(entities)) {
            for (var entity : entities) {
                params.put(MybatisSqlProvider.ENTITY, entity);
                MybatisSqlProvider.putEntityClass(params, entity.getClass());
                keyGenerator.processBefore(executor, ms, st, param);
            }
        }
    }

    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement st, Object param) {
        //批量插入不支持回写ID，意义不大影响性能。
    }
}
