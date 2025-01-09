package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.core.MybatisSqlProvider;
import cn.com.idmy.orm.core.Tables;
import cn.com.idmy.orm.mybatis.handler.EnumTypeHandler;
import cn.com.idmy.orm.mybatis.handler.JsonArrayTypeHandler;
import cn.com.idmy.orm.mybatis.handler.JsonObjectTypeHandler;
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
    public MybatisConfiguration() {
        var registry = getTypeHandlerRegistry();
        registry.setDefaultEnumTypeHandler(EnumTypeHandler.class);
        registry.register(JsonObjectTypeHandler.class);
        registry.register(JsonArrayTypeHandler.class);
    }

    public void register(@NotNull TypeHandler<?> handler) {
        getTypeHandlerRegistry().register(handler);
    }

    @Override
    public ParameterHandler newParameterHandler(@NotNull MappedStatement ms, @NotNull Object param, @NotNull BoundSql boundSql) {
        if (!ms.getId().endsWith(SELECT_KEY_SUFFIX)) {
            if (param instanceof Map<?, ?> map && map.containsKey(MybatisSqlProvider.SQL_PARAMS)) {
                var handler = new MybatisParameterHandler(ms, param, boundSql);
                return (ParameterHandler) interceptorChain.pluginAll(handler);
            }
        }
        return super.newParameterHandler(ms, param, boundSql);
    }

    @Override
    public void addMappedStatement(@NotNull MappedStatement ms) {
        var table = Tables.getTable(ms.getId().substring(0, ms.getId().lastIndexOf(".")));
        var msId = ms.getId();
        if (StrUtil.endWithAny(msId, "." + MybatisSqlProvider.create, "." + MybatisSqlProvider.creates) && ms.getKeyGenerator() == NoKeyGenerator.INSTANCE) {
            ms = MybatisModifier.replaceIdGenerator(ms, table);
        } else if (StrUtil.endWith(msId, "." + MybatisSqlProvider.count)) {
            ms = MybatisModifier.replaceCountAsteriskResultMap(ms);
        } else if (StrUtil.endWithAny(msId, "." + MybatisSqlProvider.getNullable, "." + MybatisSqlProvider.find0)) {
            ms = MybatisModifier.replaceQueryResultMap(ms, table);
        } else if (ms.getSqlCommandType() == SqlCommandType.SELECT) {

        }
        super.addMappedStatement(ms);
    }
}
