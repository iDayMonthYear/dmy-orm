package cn.com.idmy.orm.core.keygen;

import cn.com.idmy.orm.annotation.KeyType;
import cn.com.idmy.orm.core.OrmConsts;
import cn.com.idmy.orm.core.exception.OrmExceptions;
import cn.com.idmy.orm.core.row.Row;
import cn.com.idmy.orm.core.row.RowKey;
import cn.com.idmy.orm.core.util.StringUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;

import java.sql.Statement;
import java.util.Map;

/**
 * 通过 java 编码的方式生成主键
 * 当主键类型配置为 KeyType#Generator 时，使用此生成器生成
 * {@link KeyType#Generator}
 */
public class RowCustomKeyGenerator implements KeyGenerator {

    protected RowKey rowKey;
    protected IKeyGenerator keyGenerator;


    public RowCustomKeyGenerator(RowKey rowKey) {
        this.rowKey = rowKey;
        this.keyGenerator = KeyGeneratorFactory.getKeyGenerator(rowKey.getValue());

        ensuresKeyGeneratorNotNull();
    }

    private void ensuresKeyGeneratorNotNull() {
        if (keyGenerator == null) {
            throw OrmExceptions.wrap("The name of \"%s\" key generator not exist.", rowKey.getValue());
        }
    }


    @Override
    public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        Row row = (Row) ((Map) parameter).get(OrmConsts.ROW);
        try {
            Object existId = row.get(rowKey.getKeyColumn());
            // 若用户主动设置了主键，则使用用户自己设置的主键，不再生成主键
            // 只有主键为 null 或者 空字符串时，对主键进行设置
            if (existId == null || (existId instanceof String && StringUtil.isBlank((String) existId))) {
                Object generateId = keyGenerator.generate(row, rowKey.getKeyColumn());
                row.put(rowKey.getKeyColumn(), generateId);
            }
        } catch (Exception e) {
            throw OrmExceptions.wrap(e);
        }
    }


    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        //do nothing
    }

}
