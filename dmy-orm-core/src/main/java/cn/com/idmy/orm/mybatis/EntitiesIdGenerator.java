package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.core.SqlProvider;
import jakarta.validation.constraints.NotNull;
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
    public void processBefore(@NotNull Executor executor, @NotNull MappedStatement ms, @NotNull Statement st, @NotNull Object param) {
        var params = (Map<String, Object>) param;
        var ls = SqlProvider.listEntity(params);
        if (CollUtil.isNotEmpty(ls)) {
            for (var entity : ls) {
                params.put(SqlProvider.ENTITY, entity);
                SqlProvider.putEntityType(params, entity.getClass());
                keyGenerator.processBefore(executor, ms, st, param);
            }
        }
    }

    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement st, Object param) {
        //批量插入不支持回写ID，意义不大影响性能。
    }
}
