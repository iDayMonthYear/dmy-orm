package cn.com.idmy.orm.mybatis;

import cn.com.idmy.base.annotation.Table.IdType;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.TableInfo;
import lombok.NoArgsConstructor;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.dromara.hutool.core.text.StrUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.BiConsumer;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class MybatisUtil {
    public static KeyGenerator createKeyGenerator(@NotNull MappedStatement ms, @NotNull TableInfo table) {
        var tableId = table.id();
        var idType = tableId.idType();
        if (idType == IdType.NONE) {
            return NoKeyGenerator.INSTANCE;
        } else if (idType == IdType.AUTO) {
            return Jdbc3KeyGenerator.INSTANCE;
        } else if (idType == IdType.GENERATOR) {
            return new CustomIdGenerator(ms.getConfiguration(), table);
        } else {
            var sequence = tableId.value();
            if (StrUtil.isBlank(sequence)) {
                throw new OrmException(StrUtil.format("Please config sequence by @Table.Id(value=\"...\") for field: {} in class: {}", tableId.name(), table.entityType().getSimpleName()));
            } else {
                return MybatisModifier.getSelectKeyGenerator(ms, tableId);
            }
        }
    }

    public static <M, E> int[] batch(SqlSessionFactory factory, Collection<E> ls, int size, Class<M> mapperClass, BiConsumer<M, E> consumer) {
        if (ls == null || ls.isEmpty()) {
            return new int[]{0};
        }
        int[] out = new int[ls.size()];
        try (var session = factory.openSession(ExecutorType.BATCH, true)) {
            M mapper = session.getMapper(mapperClass);
            int counter = 0;
            int resultsIdx = 0;
            for (E d : ls) {
                consumer.accept(mapper, d);
                if (++counter == size) {
                    counter = 0;
                    var results = session.flushStatements();
                    for (int i = 0, batchResultsSize = results.size(); i < batchResultsSize; i++) {
                        var result = results.get(i);
                        var counts = result.getUpdateCounts();
                        for (int j = 0, countsLength = counts.length; j < countsLength; j++) {
                            var updateCount = counts[j];
                            out[resultsIdx++] = updateCount;
                        }
                    }
                }
            }
            if (counter != 0) {
                var results = session.flushStatements();
                for (int i = 0, batchResultsSize = results.size(); i < batchResultsSize; i++) {
                    var result = results.get(i);
                    var counts = result.getUpdateCounts();
                    for (int j = 0, countsLength = counts.length; j < countsLength; j++) {
                        var updateCount = counts[j];
                        out[resultsIdx++] = updateCount;
                    }
                }
            }
        }
        return out;
    }
}