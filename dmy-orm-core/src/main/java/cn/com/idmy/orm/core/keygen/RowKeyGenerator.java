package cn.com.idmy.orm.core.keygen;

import cn.com.idmy.orm.annotation.KeyType;
import cn.com.idmy.orm.core.OrmConsts;
import cn.com.idmy.orm.core.row.Row;
import cn.com.idmy.orm.core.row.RowCPI;
import cn.com.idmy.orm.core.row.RowKey;
import cn.com.idmy.orm.core.util.ArrayUtil;
import cn.com.idmy.orm.core.util.CollectionUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 为 row 的主键生成器
 */
public class RowKeyGenerator implements KeyGenerator, IMultiKeyGenerator {

    private static final KeyGenerator[] NO_KEY_GENERATORS = new KeyGenerator[0];

    private final MappedStatement ms;
    private KeyGenerator[] keyGenerators;
    private Set<String> autoKeyGeneratorNames;

    public RowKeyGenerator(MappedStatement methodMappedStatement) {
        this.ms = methodMappedStatement;
    }

    @Override
    public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        Row row = (Row) ((Map<?, ?>) parameter).get(OrmConsts.ROW);
        keyGenerators = buildRowKeyGenerators(RowCPI.obtainsPrimaryKeys(row));
        for (KeyGenerator keyGenerator : keyGenerators) {
            keyGenerator.processBefore(executor, ms, stmt, parameter);
        }
    }


    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        for (KeyGenerator keyGenerator : keyGenerators) {
            keyGenerator.processAfter(executor, ms, stmt, parameter);
        }
    }


    private KeyGenerator[] buildRowKeyGenerators(RowKey[] rowKeys) {
        if (ArrayUtil.isEmpty(rowKeys)) {
            return NO_KEY_GENERATORS;
        }

        KeyGenerator[] keyGenerators = new KeyGenerator[rowKeys.length];
        for (int i = 0; i < rowKeys.length; i++) {
            KeyGenerator keyGenerator = createByRowKey(rowKeys[i]);
            keyGenerators[i] = keyGenerator;
        }
        return keyGenerators;
    }


    private KeyGenerator createByRowKey(RowKey rowKey) {
        if (rowKey == null || rowKey.getKeyType() == KeyType.NONE) {
            return NoKeyGenerator.INSTANCE;
        }

        String keyColumn = rowKey.getKeyColumn();
        if (rowKey.getKeyType() == KeyType.AUTO) {
            if (autoKeyGeneratorNames == null) {
                autoKeyGeneratorNames = new HashSet<>();
            }
            autoKeyGeneratorNames.add(keyColumn);
            return new RowJdbc3KeyGenerator(keyColumn);
        }

        if (rowKey.getKeyType() == KeyType.GENERATOR) {
            return new RowCustomKeyGenerator(rowKey);
        }
        //通过数据库的 sequence 生成主键
        else {
            String selectId = "row." + SelectKeyGenerator.SELECT_KEY_SUFFIX;
            String sequence = rowKey.getValue();
            SqlSource sqlSource = ms.getLang().createSqlSource(ms.getConfiguration(), sequence.trim(), Object.class);
            MappedStatement.Builder msBuilder = new MappedStatement.Builder(ms.getConfiguration(), selectId, sqlSource, SqlCommandType.SELECT)
                    .resource(ms.getResource())
                    .fetchSize(null)
                    .timeout(null)
                    .statementType(StatementType.PREPARED)
                    .keyGenerator(NoKeyGenerator.INSTANCE)
                    .keyProperty(OrmConsts.ROW + "." + keyColumn)
                    .keyColumn(keyColumn)
                    .databaseId(ms.getDatabaseId())
                    .lang(ms.getLang())
                    .resultOrdered(false)
                    .resultSets(null)
                    .resultMaps(new ArrayList<>())
                    .resultSetType(null)
                    .flushCacheRequired(false)
                    .useCache(false)
                    .cache(ms.getCache());

            MappedStatement keyMappedStatement = msBuilder.build();
            ms.getConfiguration().addMappedStatement(keyMappedStatement);

            //看到有的框架把 keyGenerator 添加到 mybatis 的当前配置里去，其实是完全没必要的
            //因为只有在 xml 解析的时候，才可能存在多一个 MappedStatement 拥有同一个 keyGenerator 的情况
            //当前每个方法都拥有一个自己的 keyGenerator 了，没必要添加
            //addKeyGenerator(selectId, keyGenerator)
            return new SelectKeyGenerator(keyMappedStatement, rowKey.isBefore());
        }

    }

    /**
     * 是否需要数据库生成主键
     *
     * @return true 需要生成
     */
    @Override
    public boolean hasGeneratedKeys() {
        return CollectionUtil.isNotEmpty(autoKeyGeneratorNames);
    }

    /**
     * 数据库主键定义的 key
     *
     * @return key 数组
     */
    @Override
    public String[] getKeyColumnNames() {
        return autoKeyGeneratorNames == null ? new String[0] : autoKeyGeneratorNames.toArray(new String[0]);
    }

}
