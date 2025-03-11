package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.core.PageInterceptor;
import cn.com.idmy.orm.core.SqlProvider;
import cn.com.idmy.orm.core.Tables;
import cn.com.idmy.orm.mybatis.handler.*;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.LocalCacheScope;
import org.dromara.hutool.core.text.StrUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static cn.com.idmy.orm.mybatis.MybatisModifier.replaceCountAsteriskResultMap;
import static cn.com.idmy.orm.mybatis.MybatisModifier.replaceIdGenerator;
import static cn.com.idmy.orm.mybatis.MybatisModifier.replaceQueryResultMap;
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
        addInterceptor(new PageInterceptor());
        setLocalCacheScope(LocalCacheScope.STATEMENT);
    }

    @Override
    public ParameterHandler newParameterHandler(@NotNull MappedStatement ms, @NotNull Object param, @NotNull BoundSql sql) {
        if (!ms.getId().endsWith(SELECT_KEY_SUFFIX)) {
            if (param instanceof Map<?, ?> map && map.containsKey(SqlProvider.SQL_PARAMS)) {
                var handler = new MybatisParameterHandler(ms, param, sql);
                return (ParameterHandler) interceptorChain.pluginAll(handler);
            }
        }
        return super.newParameterHandler(ms, param, sql);
    }

    @Override
    public void addMappedStatement(@NotNull MappedStatement ms) {
        var stId = ms.getId();
        var table = Tables.getTable(stId.substring(0, stId.lastIndexOf(DOT)));
        if (StrUtil.endWithAny(stId, DOT + SqlProvider.create, DOT + SqlProvider.creates) && ms.getKeyGenerator() == NoKeyGenerator.INSTANCE) {
            ms = replaceIdGenerator(ms, table);
        } else if (StrUtil.endWith(stId, DOT + SqlProvider.count)) {
            ms = replaceCountAsteriskResultMap(ms);
        } else if (StrUtil.endWithAny(stId, DOT + SqlProvider.getNullable0, DOT + SqlProvider.list0)) {
            ms = replaceQueryResultMap(ms, table);
        } else if (ms.getSqlCommandType() == SqlCommandType.SELECT) {
            for (var resultMap : ms.getResultMaps()) {
                var clazz = resultMap.getType();
                if (isDefaultResultMap(stId, resultMap.getId())) {
                    ms = replaceQueryResultMap(ms, Tables.getTable(clazz));
                }
            }
        }
        super.addMappedStatement(ms);
    }

    private boolean isDefaultResultMap(String stId, String resultMapId) {
        return resultMapId.equals(stId + "-Inline");
    }
}
