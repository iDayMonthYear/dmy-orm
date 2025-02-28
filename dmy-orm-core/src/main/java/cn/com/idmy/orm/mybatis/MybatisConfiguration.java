package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.core.SqlProvider;
import cn.com.idmy.orm.core.Tables;
import cn.com.idmy.orm.mybatis.handler.*;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;
import org.dromara.hutool.core.text.StrUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static org.apache.ibatis.executor.keygen.SelectKeyGenerator.SELECT_KEY_SUFFIX;

class MybatisConfiguration extends Configuration {
    private static final String DOT = ".";

    public MybatisConfiguration() {
        var registry = getTypeHandlerRegistry();
        registry.setDefaultEnumTypeHandler(EnumTypeHandler.class);
        registry.register(JsonObjectTypeHandler.class);
        registry.register(JsonArrayTypeHandler.class);
        registry.register(ListIntegerTypeHandler.class);
        registry.register(ListLongTypeHandler.class);
        registry.register(ListStringTypeHandler.class);
    }

    public void register(@NotNull TypeHandler<?> handler) {
        getTypeHandlerRegistry().register(handler);
    }

    @Override
    public ParameterHandler newParameterHandler(@NotNull MappedStatement ms, @NotNull Object param, @NotNull BoundSql boundSql) {
        if (!ms.getId().endsWith(SELECT_KEY_SUFFIX)) {
            if (param instanceof Map<?, ?> map && map.containsKey(SqlProvider.SQL_PARAMS)) {
                var handler = new MybatisParameterHandler(ms, param, boundSql);
                return (ParameterHandler) interceptorChain.pluginAll(handler);
            }
        }
        return super.newParameterHandler(ms, param, boundSql);
    }

    @Override
    public void addMappedStatement(@NotNull MappedStatement ms) {
        var table = Tables.getTable(ms.getId().substring(0, ms.getId().lastIndexOf(DOT)));
        var msId = ms.getId();
        if (StrUtil.endWithAny(msId, DOT + SqlProvider.create, DOT + SqlProvider.creates) && ms.getKeyGenerator() == NoKeyGenerator.INSTANCE) {
            ms = MybatisModifier.replaceIdGenerator(ms, table);
        } else if (StrUtil.endWith(msId, DOT + SqlProvider.count)) {
            ms = MybatisModifier.replaceCountAsteriskResultMap(ms);
        } else if (StrUtil.endWithAny(msId, DOT + SqlProvider.getNullable, DOT + SqlProvider.list)) {
            ms = MybatisModifier.replaceQueryResultMap(ms, table);
        } else if (ms.getSqlCommandType() == SqlCommandType.SELECT) {

        }
        super.addMappedStatement(ms);
    }
}
