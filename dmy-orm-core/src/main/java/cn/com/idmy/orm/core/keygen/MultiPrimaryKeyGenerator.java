package cn.com.idmy.orm.core.keygen;

import cn.com.idmy.orm.core.table.IdInfo;
import cn.com.idmy.orm.core.table.TableInfo;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 多主键、复合主键 id 生成器
 */
public class MultiPrimaryKeyGenerator implements KeyGenerator, IMultiKeyGenerator {

    private final List<KeyGenerator> keyGenerators;

    //所有自增字段的名称
    private final String[] autoGenKeyColumnNames;

    public MultiPrimaryKeyGenerator(MappedStatement mappedStatement, TableInfo tableInfo, List<IdInfo> primaryKeyList) {
        this.keyGenerators = new ArrayList<>();

        List<String> autoGenKeyColumnNameList = new ArrayList<>();
        for (IdInfo idInfo : primaryKeyList) {
            KeyGenerator idKeyGenerator = MybatisKeyGeneratorUtil.createIdKeyGenerator(tableInfo, mappedStatement, idInfo);
            keyGenerators.add(idKeyGenerator);
            if (idKeyGenerator == Jdbc3KeyGenerator.INSTANCE) {
                autoGenKeyColumnNameList.add(idInfo.getColumn());
            }
        }

        this.autoGenKeyColumnNames = autoGenKeyColumnNameList.toArray(new String[0]);
    }

    @Override
    public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
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

    /**
     * 是否需要数据库 自动生成主键
     *
     * @return true: need generated keys
     */
    @Override
    public boolean hasGeneratedKeys() {
        for (KeyGenerator keyGenerator : keyGenerators) {
            if (keyGenerator == Jdbc3KeyGenerator.INSTANCE) {
                return true;
            }
        }
        return false;
    }

    /**
     * 自动生成主键的 columns 字段
     *
     * @return keyColumnNames
     */
    @Override
    public String[] getKeyColumnNames() {
        return autoGenKeyColumnNames;
    }


}
