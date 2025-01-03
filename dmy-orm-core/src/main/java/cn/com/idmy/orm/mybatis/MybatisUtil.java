package cn.com.idmy.orm.mybatis;

import cn.com.idmy.base.annotation.Table.IdType;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.TableInfo;
import lombok.NoArgsConstructor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.dromara.hutool.core.text.StrUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
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
                throw new OrmException(StrUtil.format("Please config sequence by @Table.Id(value=\"...\") for field: {} in class: {}", tableId.name(), table.entityClass().getSimpleName()));
            } else {
                return MybatisModifier.getSelectKeyGenerator(ms, tableId);
            }
        }
    }

    public static <M, E> int[] executeBatch(SqlSessionFactory sqlSessionFactory, Collection<E> ls, int size, Class<M> mapperClass, BiConsumer<M, E> consumer) {
        if (ls == null || ls.isEmpty()) {
            return new int[]{0};
        }
        int[] results = new int[ls.size()];
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, true)) {
            M mapper = sqlSession.getMapper(mapperClass);
            int counter = 0;
            int resultsPos = 0;
            for (E d : ls) {
                consumer.accept(mapper, d);
                if (++counter == size) {
                    counter = 0;
                    List<BatchResult> batchResults = sqlSession.flushStatements();
                    for (BatchResult batchResult : batchResults) {
                        int[] updateCounts = batchResult.getUpdateCounts();
                        for (int updateCount : updateCounts) {
                            results[resultsPos++] = updateCount;
                        }
                    }
                }
            }
            if (counter != 0) {
                List<BatchResult> batchResults = sqlSession.flushStatements();
                for (BatchResult batchResult : batchResults) {
                    int[] updateCounts = batchResult.getUpdateCounts();
                    for (int updateCount : updateCounts) {
                        results[resultsPos++] = updateCount;
                    }
                }
            }
        }
        return results;
    }
}