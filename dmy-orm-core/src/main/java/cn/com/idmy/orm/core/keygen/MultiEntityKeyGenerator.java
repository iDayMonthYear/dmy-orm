package cn.com.idmy.orm.core.keygen;

import cn.com.idmy.orm.core.OrmConsts;
import cn.com.idmy.orm.core.util.CollectionUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;

import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * 多实体主键生成器，用于批量插入的场景
 */
public class MultiEntityKeyGenerator implements KeyGenerator {

    private final KeyGenerator keyGenerator;

    public MultiEntityKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    @Override
    public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        List<Object> entities = (List<Object>) ((Map) parameter).get(OrmConsts.ENTITIES);
        if (CollectionUtil.isNotEmpty(entities)) {
            for (Object entity : entities) {
                ((Map) parameter).put(OrmConsts.ENTITY, entity);
                keyGenerator.processBefore(executor, ms, stmt, parameter);
            }
        }
    }


    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        // do nothing
        // 多条数据批量插入的场景下，不支持后设置主键
        // 比如 INSERT INTO `tb_account`(uuid,name,sex) VALUES (?, ?, ?), (?, ?, ?)
    }

}
