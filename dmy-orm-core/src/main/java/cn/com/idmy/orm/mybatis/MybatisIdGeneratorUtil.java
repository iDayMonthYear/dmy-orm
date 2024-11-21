package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.annotation.Table.Id.Type;
import cn.com.idmy.orm.core.MybatisSqlProvider;
import cn.com.idmy.orm.core.TableInfo;
import cn.com.idmy.orm.core.TableInfo.TableIdInfo;
import lombok.NoArgsConstructor;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.session.Configuration;
import org.dromara.hutool.core.text.StrUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class MybatisIdGeneratorUtil {
    public static KeyGenerator create(MappedStatement ms, TableInfo table) {
        var id = table.id();
        var type = id.type();
        if (type == null || type == Type.NONE) {
            return NoKeyGenerator.INSTANCE;
        } else if (type == Type.AUTO) {
            return Jdbc3KeyGenerator.INSTANCE;
        } else if (type == Type.GENERATOR) {
            return new CustomIdGenerator(ms.getConfiguration(), table);
        } else {
            var sequence = id.value();
            if (StrUtil.isBlank(sequence)) {
                throw new OrmException(StrUtil.format("Please config sequence by @Table.Id(value=\"...\") for field: {} in class: {}", id.name(), table.entityClass().getSimpleName()));
            } else {
                return getSelectKeyGenerator(ms, id);
            }
        }
    }

    private static SelectKeyGenerator getSelectKeyGenerator(MappedStatement ms, TableIdInfo id) {
        var sequence = id.value();
        var selectId = ms.getId() + SelectKeyGenerator.SELECT_KEY_SUFFIX;
        var config = ms.getConfiguration();
        var sqlSource = ms.getLang().createSqlSource(config, sequence.trim(), id.field().getType());
        var mappedStatement = new MappedStatement.Builder(config, selectId, sqlSource, SqlCommandType.SELECT)
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
        config.addMappedStatement(mappedStatement);
        return new SelectKeyGenerator(mappedStatement, id.before());
    }

    private static List<ResultMap> createIdResultMaps(Configuration cfg, String sid, Class<?> type, List<ResultMapping> mappings) {
        var resultMap = new ResultMap.Builder(cfg, sid, type, mappings, null).build();
        return Collections.singletonList(resultMap);
    }
}
