package cn.com.idmy.orm.mybatis;

import cn.com.idmy.base.annotation.Column;
import cn.com.idmy.base.util.Assert;
import cn.com.idmy.orm.OrmConfig;
import cn.com.idmy.orm.core.SqlProvider;
import cn.com.idmy.orm.core.TableInfo;
import cn.com.idmy.orm.core.TableInfo.TableId;
import cn.com.idmy.orm.core.Tables;
import lombok.NoArgsConstructor;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.mapping.ResultMapping.Builder;
import org.apache.ibatis.session.Configuration;
import org.dromara.hutool.core.reflect.FieldUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.jetbrains.annotations.NotNull;

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
        var mappings = new ArrayList<ResultMapping>() {{
            add(new Builder(cfg, id.field().getName(), id.name(), id.field().getType()).flags(List.of(ResultFlag.ID)).build());
        }};
        for (var col : table.columns()) {
            var javaType = col.field().getType();
            var builder = new ResultMapping.Builder(cfg, col.field().getName(), col.name(), javaType);
            var handler = Tables.getTypeHandler(col.field());
            if (handler == null) {
                var typeHandler = cfg.getTypeHandlerRegistry().getTypeHandler(javaType, null);
                if (typeHandler == null && !col.exist()) {
                    continue;
                }
            } else {
                builder.typeHandler(handler);
            }
            mappings.add(builder.build());
        }
        cfg.addResultMap(new ResultMap.Builder(cfg, resultMapId, entityType, mappings).build());
    }

    @Deprecated
    static MappedStatement replaceNonEntityQueryResultMap(@NotNull MappedStatement ms, @NotNull Class<?> clazz) {
        var cfg = ms.getConfiguration();
        var resultMapId = clazz.getName() + ".CustomResultMap";
        if (!cfg.hasResultMap(resultMapId)) {
            var mappings = new ArrayList<ResultMapping>();
            var fields = FieldUtil.getFields(clazz);
            for (var field : fields) {
                if (field.isAnnotationPresent(Column.class) && !field.getAnnotation(Column.class).exist()) {
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
}
