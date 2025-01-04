package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.core.MybatisSqlProvider;
import cn.com.idmy.orm.core.Tables;
import cn.com.idmy.orm.mybatis.handler.EnumTypeHandler;
import cn.com.idmy.orm.mybatis.handler.JsonArrayTypeHandler;
import cn.com.idmy.orm.mybatis.handler.JsonObjectTypeHandler;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.dromara.hutool.core.text.StrUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

class MybatisConfiguration extends Configuration {
    public MybatisConfiguration() {
        var registry = getTypeHandlerRegistry();
        registry.setDefaultEnumTypeHandler(EnumTypeHandler.class);
        registry.register(JsonObjectTypeHandler.class);
        registry.register(JsonArrayTypeHandler.class);
    }

    @Override
    public ParameterHandler newParameterHandler(@NotNull MappedStatement ms, @NotNull Object param, @NotNull BoundSql boundSql) {
        if (!ms.getId().endsWith(SelectKeyGenerator.SELECT_KEY_SUFFIX)) {
            if (param instanceof Map<?, ?> map && map.containsKey(MybatisSqlProvider.SQL_PARAMS)) {
                var handler = new MybatisParameterHandler(ms, param, boundSql);
                return (ParameterHandler) interceptorChain.pluginAll(handler);
            }
        }
        return super.newParameterHandler(ms, param, boundSql);
    }

    @Override
    public void addMappedStatement(@NotNull MappedStatement ms) {
        var table = Tables.getTable(ms);
        if (StrUtil.endWithAny(ms.getId(), MybatisSqlProvider.create, MybatisSqlProvider.creates) && ms.getKeyGenerator() == NoKeyGenerator.INSTANCE) {
            super.addMappedStatement(MybatisModifier.replaceIdGenerator(ms, table));
        } else if (ms.getSqlCommandType() == SqlCommandType.SELECT) {
            super.addMappedStatement(MybatisModifier.addSelectResultMap(ms, table));
        } else {
            super.addMappedStatement(ms);
        }
    }
}
