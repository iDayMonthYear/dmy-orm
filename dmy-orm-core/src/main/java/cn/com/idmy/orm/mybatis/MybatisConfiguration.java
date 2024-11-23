package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.core.MybatisSqlProvider;
import cn.com.idmy.orm.core.TableInfo;
import cn.com.idmy.orm.core.Tables;
import cn.com.idmy.orm.mybatis.handler.EnumTypeHandler;
import cn.com.idmy.orm.mybatis.handler.JsonArrayTypeHandler;
import cn.com.idmy.orm.mybatis.handler.JsonObjectTypeHandler;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.mapping.ResultMapping.Builder;
import org.apache.ibatis.session.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

class MybatisConfiguration extends Configuration {
    public MybatisConfiguration() {
        var registry = getTypeHandlerRegistry();
        registry.setDefaultEnumTypeHandler(EnumTypeHandler.class);
        registry.register(JsonObjectTypeHandler.class);
        registry.register(JsonArrayTypeHandler.class);
    }

    @Override
    public ParameterHandler newParameterHandler(MappedStatement ms, Object param, BoundSql boundSql) {
        var msId = ms.getId();
        if (!msId.endsWith(SelectKeyGenerator.SELECT_KEY_SUFFIX)) {
            if (param instanceof Map<?, ?> map && map.containsKey(MybatisSqlProvider.SQL_PARAMS)) {
                var handler = new MybatisParameterHandler(ms, param, boundSql);
                return (ParameterHandler) interceptorChain.pluginAll(handler);
            }
        }
        return super.newParameterHandler(ms, param, boundSql);
    }

    @Override
    public void addMappedStatement(MappedStatement ms) {
        TableInfo tableInfo = Tables.getTableInfo(ms);
        ms = replaceIdGenerator(ms, tableInfo);
        ms = addResultMap(ms, tableInfo);
        super.addMappedStatement(ms);
    }

    private MappedStatement addResultMap(MappedStatement ms, TableInfo tableInfo) {
        var resultMapId = tableInfo.entityClass().getName() + ".BaseResultMap";
        // 确保 ResultMap 已创建
        if (!hasResultMap(resultMapId)) {
            addResultMap(tableInfo.entityClass(), tableInfo, resultMapId);
        }

        // 替换为实体类的 ResultMap
        var resultMaps = new ArrayList<ResultMap>(1);
        resultMaps.add(getResultMap(resultMapId));

        return new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), ms.getSqlSource(), ms.getSqlCommandType())
                .resource(ms.getResource())
                .fetchSize(ms.getFetchSize())
                .timeout(ms.getTimeout())
                .statementType(ms.getStatementType())
                .keyGenerator(ms.getKeyGenerator())
                .keyProperty(ms.getKeyProperties() == null ? null : String.join(",", ms.getKeyProperties()))
                .keyColumn(ms.getKeyColumns() == null ? null : String.join(",", ms.getKeyColumns()))
                .databaseId(ms.getDatabaseId())
                .lang(ms.getLang())
                .resultOrdered(ms.isResultOrdered())
                .resultSets(ms.getResultSets() == null ? null : String.join(",", ms.getResultSets()))
                .resultMaps(resultMaps)  // 使用实体类的 ResultMap
                .resultSetType(ms.getResultSetType())
                .flushCacheRequired(ms.isFlushCacheRequired())
                .useCache(ms.isUseCache())
                .cache(ms.getCache())
                .build();
    }

    public void addResultMap(Class<?> entityClass, TableInfo table, String resultMapId) {
        var resultMappings = new ArrayList<ResultMapping>();

        // 添加ID映射
        var id = table.id();
        var resultMapping = new Builder(this, id.field().getName(), id.name(), id.field().getType()).flags(Collections.singletonList(ResultFlag.ID)).build();
        resultMappings.add(resultMapping);

        // 添加普通列映射
        for (var column : table.columns()) {
            var builder = new ResultMapping.Builder(this, column.field().getName(), column.name(), column.field().getType());

            // 如果有TypeHandler，设置到ResultMapping中
            var handler = Tables.getHandler(column.field());
            if (handler != null) {
                builder.typeHandler(handler);
            }
            resultMappings.add(builder.build());
        }

        // 创建并添加ResultMap
        addResultMap(new ResultMap.Builder(this, resultMapId, entityClass, resultMappings).build());
    }

    private MappedStatement replaceIdGenerator(MappedStatement ms, TableInfo tableInfo) {
        if (ms.getKeyGenerator() == NoKeyGenerator.INSTANCE) {
            String msId = ms.getId();
            if (msId.endsWith(MybatisSqlProvider.insert) || msId.endsWith(MybatisSqlProvider.inserts)) {
                return replaceIdGenerator0(ms, tableInfo);
            }
        }
        return ms;
    }

    private MappedStatement replaceIdGenerator0(MappedStatement ms, TableInfo tableInfo) {
        var generator = MybatisIdGeneratorUtil.create(ms, tableInfo);
        if (generator == NoKeyGenerator.INSTANCE) {
            return ms;
        }

        if (ms.getId().endsWith(MybatisSqlProvider.inserts)) {
            generator = new EntitiesIdGenerator(generator);
        }

        return new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), ms.getSqlSource(), ms.getSqlCommandType())
                .resource(ms.getResource())
                .fetchSize(ms.getFetchSize())
                .timeout(ms.getTimeout())
                .statementType(ms.getStatementType())
                .keyGenerator(generator) // 替换主键生成器
                .keyProperty(MybatisSqlProvider.ENTITY + "." + tableInfo.id().field().getName())
                .keyColumn(tableInfo.id().name())
                .databaseId(databaseId)
                .lang(ms.getLang())
                .resultOrdered(ms.isResultOrdered())
                .resultSets(ms.getResultSets() == null ? null : String.join(",", ms.getResultSets()))
                .resultMaps(ms.getResultMaps())
                .resultSetType(ms.getResultSetType())
                .flushCacheRequired(ms.isFlushCacheRequired())
                .useCache(ms.isUseCache())
                .cache(ms.getCache())
                .build();
    }
}
