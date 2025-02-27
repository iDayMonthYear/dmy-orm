package cn.com.idmy.orm.mybatis;

import cn.com.idmy.base.util.Assert;
import cn.com.idmy.orm.core.SqlProvider;
import cn.com.idmy.orm.core.TableInfo;
import cn.com.idmy.orm.core.TableInfo.TableId;
import cn.com.idmy.orm.core.Tables;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.mapping.ResultMapping.Builder;
import org.apache.ibatis.session.Configuration;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
class MybatisModifier {
    static SelectKeyGenerator getSelectKeyGenerator(@NotNull MappedStatement ms, @NotNull TableId id) {
        var cfg = ms.getConfiguration();
        var seq = Assert.notNull(id.value(), "序列不能为空");
        var selectId = ms.getId() + SelectKeyGenerator.SELECT_KEY_SUFFIX;
        var idField = id.field();
        var sqlSource = ms.getLang().createSqlSource(cfg, seq.trim(), idField.getType());
        var keyProperty = SqlProvider.ENTITY + "." + idField.getName();
        var idResultMaps = List.of(new ResultMap.Builder(cfg, selectId + "-Inline", idField.getType(), new ArrayList<>(), null).build());
        var newMs = new MappedStatement
                .Builder(cfg, selectId, sqlSource, SqlCommandType.SELECT)
                .statementType(StatementType.PREPARED)
                .keyGenerator(NoKeyGenerator.INSTANCE)
                .keyProperty(keyProperty)
                .databaseId(ms.getDatabaseId())
                .resource(ms.getResource())
                .resultMaps(idResultMaps)
                .cache(ms.getCache())
                .keyColumn(id.name())
                .lang(ms.getLang())
                .flushCacheRequired(false)
                .resultOrdered(false)
                .useCache(false)
                .resultSetType(null)
                .resultSets(null)
                .timeout(null)
                .fetchSize(null)
                .build();
        cfg.addMappedStatement(newMs);
        return new SelectKeyGenerator(newMs, id.before());
    }

    protected static MappedStatement replaceIdGenerator(@NotNull MappedStatement ms, @NotNull TableInfo table) {
        var generator = MybatisUtil.createKeyGenerator(ms, table);
        if (generator == NoKeyGenerator.INSTANCE) {
            return ms;
        }

        if (ms.getId().endsWith(SqlProvider.creates)) {
            generator = new EntitiesIdGenerator(generator);
        }

        var cfg = ms.getConfiguration();
        var keyProperty = SqlProvider.ENTITY + "." + table.id().field().getName();
        var resultSet = ms.getResultSets() == null ? null : String.join(",", ms.getResultSets());
        var sqlCommandType = ms.getSqlCommandType();
        var sqlSource = ms.getSqlSource();
        return new MappedStatement
                .Builder(cfg, ms.getId(), sqlSource, sqlCommandType)
                .resource(ms.getResource())
                .fetchSize(ms.getFetchSize())
                .timeout(ms.getTimeout())
                .statementType(ms.getStatementType())
                .keyGenerator(generator)
                .keyProperty(keyProperty)
                .keyColumn(table.id().name())
                .databaseId(ms.getDatabaseId())
                .lang(ms.getLang())
                .resultOrdered(ms.isResultOrdered())
                .resultSets(resultSet)
                .resultMaps(ms.getResultMaps())
                .resultSetType(ms.getResultSetType())
                .flushCacheRequired(ms.isFlushCacheRequired())
                .useCache(ms.isUseCache())
                .cache(ms.getCache())
                .build();
    }

    protected static MappedStatement replaceCountAsteriskResultMap(@NotNull MappedStatement ms) {
        var cfg = ms.getConfiguration();
        var msId = ms.getId();
        var resultMappings = List.of(new ResultMapping.Builder(cfg, "count", "count(*)", long.class).build());
        var resultMaps = List.of(new ResultMap.Builder(cfg, msId + ".CountAsteriskResultMap", long.class, resultMappings).build());
        var sqlSource = ms.getSqlSource();
        var sqlCommandType = ms.getSqlCommandType();
        return new MappedStatement.Builder(cfg, msId, sqlSource, sqlCommandType).resultMaps(resultMaps).build();
    }

    static MappedStatement replaceQueryResultMap(@NotNull MappedStatement ms, @NotNull TableInfo table) {
        var cfg = ms.getConfiguration();
        var resultMapId = table.entityType().getName() + ".QueryResultMap";
        if (!cfg.hasResultMap(resultMapId)) {
            replaceQueryResultMap(cfg, table.entityType(), table, resultMapId);
        }
        var msId = ms.getId();
        var sqlSource = ms.getSqlSource();
        var sqlCommandType = ms.getSqlCommandType();
        var resultMaps = List.of(cfg.getResultMap(resultMapId));
        return new MappedStatement.Builder(cfg, msId, sqlSource, sqlCommandType).resultMaps(resultMaps).build();
    }

    private static void replaceQueryResultMap(@NotNull Configuration cfg, @NotNull Class<?> entityType, @NotNull TableInfo table, @NotNull String resultMapId) {
        var id = table.id();
        var resultMappings = new ArrayList<ResultMapping>() {{
            add(new Builder(cfg, id.field().getName(), id.name(), id.field().getType()).flags(List.of(ResultFlag.ID)).build());
        }};
        for (var col : table.columns()) {
            var builder = new ResultMapping.Builder(cfg, col.field().getName(), col.name(), col.field().getType());
            var handler = Tables.getTypeHandler(col.field());
            if (handler != null) {
                builder.typeHandler(handler);
            }
            resultMappings.add(builder.build());
        }
        cfg.addResultMap(new ResultMap.Builder(cfg, resultMapId, entityType, resultMappings).build());
    }
}
