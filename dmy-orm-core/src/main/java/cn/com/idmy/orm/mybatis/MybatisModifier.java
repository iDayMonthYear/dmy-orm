package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.core.MybatisSqlProvider;
import cn.com.idmy.orm.core.TableInfo;
import cn.com.idmy.orm.core.TableInfo.TableId;
import cn.com.idmy.orm.core.Tables;
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
                .keyGenerator(generator)
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


    static MappedStatement addSelectResultMap(@NotNull MappedStatement ms, @NotNull TableInfo tableInfo) {
        var cfg = ms.getConfiguration();

        // 处理 count 查询的特殊情况
        if (ms.getId().endsWith(MybatisSqlProvider.count)) {
            var resultMappings = new ArrayList<ResultMapping>(1) {{
                add(new ResultMapping.Builder(cfg, "count", "COUNT(*)", long.class).build());
            }};
            var resultMaps = List.of(new ResultMap.Builder(cfg, ms.getId() + ".CountResultMap", long.class, resultMappings).build());
            return new MappedStatement
                    .Builder(cfg, ms.getId(), ms.getSqlSource(), ms.getSqlCommandType())
                    .resultMaps(resultMaps)
                    .build();
        }

        // 处理普通实体查询
        var resultMapId = tableInfo.entityClass().getName() + ".BaseResultMap";
        if (!cfg.hasResultMap(resultMapId)) {
            addSelectResultMap(cfg, tableInfo.entityClass(), tableInfo, resultMapId);
        }

        var resultMaps = new ArrayList<ResultMap>(1) {{
            add(cfg.getResultMap(resultMapId));
        }};

        return new MappedStatement
                .Builder(cfg, ms.getId(), ms.getSqlSource(), ms.getSqlCommandType())
                .resultMaps(resultMaps)
                .build();
    }

    private static void addSelectResultMap(@NotNull Configuration cfg, @NotNull Class<?> entityClass, @NotNull TableInfo table, @NotNull String resultMapId) {
        // 添加ID映射
        var resultMappings = new ArrayList<ResultMapping>() {{
            var id = table.id();
            add(new Builder(cfg, id.field().getName(), id.name(), id.field().getType())
                    .flags(Collections.singletonList(ResultFlag.ID))
                    .build());
        }};

        // 添加普通列映射
        for (var column : table.columns()) {
            var builder = new ResultMapping.Builder(cfg, column.field().getName(), column.name(), column.field().getType());
            // 如果有TypeHandler，设置到ResultMapping中
            var handler = Tables.getTypeHandler(column.field());
            if (handler != null) {
                builder.typeHandler(handler);
            }
            resultMappings.add(builder.build());
        }

        // 创建并添加ResultMap
        cfg.addResultMap(new ResultMap.Builder(cfg, resultMapId, entityClass, resultMappings).build());
    }
}
