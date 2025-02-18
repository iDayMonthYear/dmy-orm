package cn.com.idmy.orm.mybatis;


import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.SqlProvider;
import cn.com.idmy.orm.core.TableInfo;
import cn.com.idmy.orm.core.TableInfo.TableId;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.dromara.hutool.core.reflect.FieldUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.jetbrains.annotations.NotNull;

import java.sql.Statement;
import java.util.Map;

public class CustomIdGenerator implements KeyGenerator {
    protected final Configuration configuration;
    protected final TableInfo table;
    protected final TableId id;
    @NotNull
    protected IdGenerator idGenerator;

    public CustomIdGenerator(@NotNull Configuration cfg, @NotNull TableInfo table) {
        this.configuration = cfg;
        this.table = table;
        this.id = table.id();
        var value = this.id.value();
        if (StrUtil.isBlank(value)) {
            value = "DB";
        }
        var generator = IdGeneratorFactory.getGenerator(value);
        if (generator == null) {
            throw new OrmException("未找到ID生成器：{}", value);
        } else {
            this.idGenerator = generator;
        }
    }

    @Override
    public void processBefore(Executor executor, MappedStatement ms, Statement st, Object parameter) {
        Object entity = ((Map<?, ?>) parameter).get(SqlProvider.ENTITY);
        try {
            Object existId = FieldUtil.getFieldValue(entity, id.field().getName());
            if (existId == null || (existId instanceof String str && StrUtil.isNotBlank(str))) {
                var newId = idGenerator.generate(entity, id.name());
                var metaObject = configuration.newMetaObject(parameter).metaObjectForProperty(SqlProvider.ENTITY);
                this.setValue(metaObject, id.field().getName(), newId);
            }
        } catch (Exception e) {
            throw new OrmException("获取自定义ID异常：{}", e.getMessage(), e);
        }
    }

    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {

    }

    private void setValue(MetaObject metaParam, String fieldName, Object value) {
        if (!metaParam.hasSetter(fieldName)) {
            throw new ExecutorException("No setter found for the keyProperty '" + fieldName + "' in " + metaParam.getOriginalObject().getClass().getName() + ".");
        } else {
            metaParam.setValue(fieldName, value);
        }
    }
}