package cn.com.idmy.orm.mybatis;


import cn.com.idmy.base.IdGenerator;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.SqlProvider;
import cn.com.idmy.orm.core.TableInfo;
import cn.com.idmy.orm.core.TableInfo.TableId;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.dromara.hutool.core.convert.ConvertUtil;
import org.dromara.hutool.core.reflect.FieldUtil;
import org.dromara.hutool.core.text.StrUtil;

import java.sql.Statement;
import java.util.Map;

public class CustomIdGenerator implements KeyGenerator {
    protected final Configuration configuration;
    protected final TableInfo table;
    protected final TableId id;
    protected IdGenerator<?> idGenerator;

    public CustomIdGenerator(@NotNull Configuration cfg, @NotNull TableInfo table) {
        this.configuration = cfg;
        this.table = table;
        this.id = table.id();
        loadIdGenerator();
    }

    protected void loadIdGenerator() {
        String key = id.key();
        if (StrUtil.isBlank(key)) {
            key = "DB";
        }
        var generator = IdGeneratorFactory.getGenerator(key);
        if (generator == null) {
            throw new OrmException("未找到 ID 生成器：{}", key);
        } else {
            this.idGenerator = generator;
        }
    }

    @Override
    public void processBefore(Executor executor, MappedStatement ms, Statement st, Object parameter) {
        var entity = ((Map<?, ?>) parameter).get(SqlProvider.ENTITY);
        try {
            var field = id.field();
            var existId = FieldUtil.getFieldValue(entity, field.getName());
            if (existId == null || (existId instanceof String str && StrUtil.isNotBlank(str))) {
                var newId = ConvertUtil.convert(field.getType(), idGenerator.generate(entity.getClass(), id.value()));
                var metaObject = configuration.newMetaObject(parameter).metaObjectForProperty(SqlProvider.ENTITY);
                this.setValue(metaObject, field.getName(), newId);
            }
        } catch (Exception e) {
            throw new OrmException("获取自定义ID异常：{}", e.getMessage(), e);
        }
    }

    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {

    }

    private void setValue(MetaObject metaParam, String fieldName, Object value) {
        if (metaParam.hasSetter(fieldName)) {
            metaParam.setValue(fieldName, value);
        } else {
            throw new ExecutorException("No setter found for the keyProperty '" + fieldName + "' in " + metaParam.getOriginalObject().getClass().getName() + ".");
        }
    }
}