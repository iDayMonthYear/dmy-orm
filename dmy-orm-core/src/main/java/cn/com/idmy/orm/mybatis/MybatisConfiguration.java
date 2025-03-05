package cn.com.idmy.orm.mybatis;

import cn.com.idmy.base.annotation.Column;
import cn.com.idmy.base.annotation.Table;
import cn.com.idmy.orm.OrmConfig;
import cn.com.idmy.orm.core.SqlProvider;
import cn.com.idmy.orm.core.Tables;
import cn.com.idmy.orm.mybatis.handler.*;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.session.Configuration;
import org.dromara.hutool.core.reflect.FieldUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
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
        var stId = ms.getId();
        var table = Tables.getTable(stId.substring(0, stId.lastIndexOf(DOT)));
        if (StrUtil.endWithAny(stId, DOT + SqlProvider.create, DOT + SqlProvider.creates) && ms.getKeyGenerator() == NoKeyGenerator.INSTANCE) {
            ms = replaceIdGenerator(ms, table);
        } else if (StrUtil.endWith(stId, DOT + SqlProvider.count)) {
            ms = replaceCountAsteriskResultMap(ms);
        } else if (StrUtil.endWithAny(stId, DOT + SqlProvider.getNullable, DOT + SqlProvider.list)) {
            ms = replaceQueryResultMap(ms, table);
        } else if (ms.getSqlCommandType() == SqlCommandType.SELECT) {
            for (var resultMap : ms.getResultMaps()) {
                var clazz = resultMap.getType();
                if (isDefaultResultMap(stId, resultMap.getId())) {
                    if (clazz.getDeclaredAnnotation(Table.class) == null) {
                        ms = createCustomResultMap(ms, clazz);
                    } else {
                        ms = replaceQueryResultMap(ms, Tables.getTable(clazz));
                    }
                }
            }
        }
        super.addMappedStatement(ms);
    }

    private MappedStatement createCustomResultMap(@NotNull MappedStatement ms, @NotNull Class<?> clazz) {
        var cfg = ms.getConfiguration();
        var resultMapId = clazz.getName() + ".CustomResultMap";

        if (!cfg.hasResultMap(resultMapId)) {
            var mappings = new ArrayList<ResultMapping>();
            var fields = FieldUtil.getFields(clazz);
            for (var field : fields) {
                if (field.isAnnotationPresent(Column.class) && field.getAnnotation(Column.class).ignore()) {
                    continue;
                }

                String columnName;
                if (field.isAnnotationPresent(Column.class)) {
                    var column = field.getAnnotation(Column.class);
                    columnName = StrUtil.isBlank(column.name()) ? OrmConfig.config().toColumnName(field.getName()) : column.name();
                } else {
                    columnName = OrmConfig.config().toColumnName(field.getName());
                }

                var builder = new ResultMapping.Builder(cfg, field.getName(), columnName, field.getType());
                var handler = Tables.getTypeHandler(field);
                if (handler != null) {
                    builder.typeHandler(handler);
                }
                mappings.add(builder.build());
            }
            cfg.addResultMap(new ResultMap.Builder(cfg, resultMapId, clazz, mappings).build());
        }
        return new MappedStatement.Builder(cfg, ms.getId(), ms.getSqlSource(), ms.getSqlCommandType()).resultMaps(List.of(cfg.getResultMap(resultMapId))).build();
    }

    private boolean isDefaultResultMap(String stId, String resultMapId) {
        return resultMapId.equals(stId + "-Inline");
    }
}
