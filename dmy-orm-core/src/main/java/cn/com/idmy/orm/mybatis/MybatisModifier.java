package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.core.MybatisSqlProvider;
import cn.com.idmy.orm.core.TableInfo;
import cn.com.idmy.orm.core.TableInfo.TableId;
import lombok.NoArgsConstructor;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.mapping.ResultMapping.Builder;
import org.apache.ibatis.session.Configuration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
class MybatisModifier {
    static SelectKeyGenerator getSelectKeyGenerator(@NotNull MappedStatement ms, @NotNull TableId id) {
        var sequence = id.value();
        var selectId = ms.getId() + SelectKeyGenerator.SELECT_KEY_SUFFIX;
        var config = ms.getConfiguration();
        var sqlSource = ms.getLang().createSqlSource(config, sequence.trim(), id.field().getType());
        var newMs = new MappedStatement.Builder(config, selectId, sqlSource, SqlCommandType.SELECT)
                .resource(ms.getResource())
                .fetchSize(null)
                .timeout(null)
                .statementType(StatementType.PREPARED)
                .keyGenerator(NoKeyGenerator.INSTANCE)
                .keyProperty(MybatisSqlProvider.ENTITY + "." + id.field().getName())
                .keyColumn(id.name())
                .databaseId(ms.getDatabaseId())
                .lang(ms.getLang())
                .resultOrdered(false)
                .resultSets(null)
                .resultMaps(createIdResultMaps(config, selectId + "-Inline", id.field().getType(), new ArrayList<>()))
                .resultSetType(null)
                .flushCacheRequired(false)
                .useCache(false)
                .cache(ms.getCache())
                .build();
        config.addMappedStatement(newMs);
        return new SelectKeyGenerator(newMs, id.before());
    }

    private static List<ResultMap> createIdResultMaps(@NotNull Configuration cfg, @NotNull String sid, @NotNull Class<?> type, @NotNull List<ResultMapping> mappings) {
        var resultMap = new ResultMap.Builder(cfg, sid, type, mappings, null).build();
        return Collections.singletonList(resultMap);
    }

    static MappedStatement replaceIdGenerator(@NotNull MappedStatement ms, @NotNull TableInfo tableInfo) {
        if (ms.getKeyGenerator() == NoKeyGenerator.INSTANCE) {
            var msId = ms.getId();
            if (msId.endsWith(MybatisSqlProvider.create) || msId.endsWith(MybatisSqlProvider.creates)) {
                return replaceIdGenerator0(ms, tableInfo);
            }
        }
        return ms;
    }

    private static MappedStatement replaceIdGenerator0(@NotNull MappedStatement ms, @NotNull TableInfo tableInfo) {
        var generator = MybatisUtil.createKeyGenerator(ms, tableInfo);
        if (generator == NoKeyGenerator.INSTANCE) {
            return ms;
        }

        if (ms.getId().endsWith(MybatisSqlProvider.creates)) {
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
                .databaseId(ms.getDatabaseId())
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


    static MappedStatement addResultMap(@NotNull MappedStatement ms, @NotNull TableInfo tableInfo) {
        var cfg = ms.getConfiguration();
        var resultMapId = tableInfo.entityClass().getName() + ".BaseResultMap";
        // 确保 ResultMap 已创建
        if (!cfg.hasResultMap(resultMapId)) {
            addResultMap(cfg, tableInfo.entityClass(), tableInfo, resultMapId);
        }

        // 替换为实体类的 ResultMap
        var resultMaps = new ArrayList<ResultMap>(1);
        resultMaps.add(cfg.getResultMap(resultMapId));

        return new MappedStatement.Builder(cfg, ms.getId(), ms.getSqlSource(), ms.getSqlCommandType())
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

    private static void addResultMap(@NotNull Configuration cfg, @NotNull Class<?> entityClass, @NotNull TableInfo table, @NotNull String resultMapId) {
        var resultMappings = new ArrayList<ResultMapping>();

        // 添加ID映射
        var id = table.id();
        resultMappings.add(new Builder(cfg, id.field().getName(), id.name(), id.field().getType()).flags(Collections.singletonList(ResultFlag.ID)).build());

        // 添加普通列映射
        for (var column : table.columns()) {
            var builder = new ResultMapping.Builder(cfg, column.field().getName(), column.name(), column.field().getType());
            // 如果有TypeHandler，设置到ResultMapping中
            var handler = column.typeHandler();
            if (handler != null) {
                builder.typeHandler(handler);
            }
            resultMappings.add(builder.build());
        }

        // 创建并添加ResultMap
        cfg.addResultMap(new ResultMap.Builder(cfg, resultMapId, entityClass, resultMappings).build());
    }
}
