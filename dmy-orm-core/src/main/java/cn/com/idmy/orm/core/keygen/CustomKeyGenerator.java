package cn.com.idmy.orm.core.keygen;

import cn.com.idmy.orm.annotation.KeyType;
import cn.com.idmy.orm.core.OrmConfig;
import cn.com.idmy.orm.core.OrmConsts;
import cn.com.idmy.orm.core.exception.OrmExceptions;
import cn.com.idmy.orm.core.table.IdInfo;
import cn.com.idmy.orm.core.table.TableInfo;
import cn.com.idmy.orm.core.util.ConvertUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.invoker.Invoker;
import org.apache.ibatis.session.Configuration;

import java.sql.Statement;
import java.util.Map;

/**
 * 通过 java 编码的方式生成主键
 * 当主键类型配置为 KeyType#Generator 时，使用此生成器生成
 * {@link KeyType#GENERATOR}
 */
public class CustomKeyGenerator implements KeyGenerator {
    protected Configuration configuration;
    protected IKeyGenerator keyGenerator;
    protected TableInfo tableInfo;
    protected IdInfo idInfo;

    public CustomKeyGenerator(Configuration configuration, TableInfo tableInfo, IdInfo idInfo) {
        this.configuration = configuration;
        OrmConfig.KeyConfig globalKeyConfig = OrmConfig.getConfig(configuration).getKeyConfig();
        String keyValue = MybatisKeyGeneratorUtil.getKeyValue(idInfo, globalKeyConfig);
        this.keyGenerator = KeyGeneratorFactory.getKeyGenerator(keyValue);
        this.tableInfo = tableInfo;
        this.idInfo = idInfo;

        ensuresKeyGeneratorNotNull();
    }

    private void ensuresKeyGeneratorNotNull() {
        if (keyGenerator == null) {
            throw OrmExceptions.wrap("The name of \"%s\" key generator not exist.\n" +
                            "please check annotation @Id(value=\"%s\") at field: %s#%s"
                    , idInfo.getValue(), idInfo.getValue(), tableInfo.getEntityClass().getName(), idInfo.getProperty());
        }
    }

    @Override
    public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        Object entity = ((Map) parameter).get(OrmConsts.ENTITY);
        try {
            Object existId = tableInfo.getValue(entity, idInfo.getProperty());

            // 若用户主动设置了主键，则使用用户自己设置的主键，不再生成主键
            // 只有主键为 null 或者 空字符串时，对主键进行设置
            if (existId == null || (existId instanceof String && StrUtil.isBlank((String) existId))) {
                Configuration configuration = ms.getConfiguration();
                MetaObject metaParam = configuration.newMetaObject(parameter);
                Object generateId = keyGenerator.generate(entity, idInfo.getColumn());
                MetaObject metaObjectForProperty = metaParam.metaObjectForProperty(OrmConsts.ENTITY);
                Invoker setInvoker = tableInfo.getReflector().getSetInvoker(idInfo.getProperty());
                Object id = ConvertUtil.convert(generateId, setInvoker.getType());
                this.setValue(metaObjectForProperty, this.idInfo.getProperty(), id);
            }
        } catch (Exception e) {
            throw OrmExceptions.wrap(e);
        }
    }

    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        //do nothing
    }

    private void setValue(MetaObject metaParam, String property, Object value) {
        if (!metaParam.hasSetter(property)) {
            throw new ExecutorException("No setter found for the keyProperty '" + property + "' in " + metaParam.getOriginalObject().getClass().getName() + ".");
        } else {
            metaParam.setValue(property, value);
        }
    }
}
