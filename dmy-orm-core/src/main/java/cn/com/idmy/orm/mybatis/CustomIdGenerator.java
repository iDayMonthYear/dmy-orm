package cn.com.idmy.orm.mybatis;


import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.SqlProvider;
import cn.com.idmy.orm.core.TableInfo;
import cn.com.idmy.orm.core.TableInfo.TableId;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.dromara.hutool.core.text.StrUtil;
import org.jetbrains.annotations.NotNull;

import java.sql.Statement;
import java.util.Map;

public class CustomIdGenerator implements KeyGenerator {
    protected final Configuration configuration;
    protected final TableInfo table;
    protected final TableId id;
    protected IdGenerator idGenerator;

    public CustomIdGenerator(@NotNull Configuration cfg, @NotNull TableInfo table) {
        this.configuration = cfg;
        this.table = table;
        this.id = table.id();
        var value = this.id.value();
        if (StrUtil.isBlank(value)) {
            value = "DB";
        }
        this.idGenerator = IdGeneratorFactory.getGenerator(value);
    }

    @Override
    public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        Object entity = ((Map<?, ?>) parameter).get(SqlProvider.ENTITY);
        try {
            /*Object existId = table.getValue(entity, id.field().getName());
            if (existId == null || (existId instanceof String str && StrUtil.isNotBlank(str))) {
                Object generateId = idGenerator.generate(entity, id.name());
                MetaObject metaObject = configuration.newMetaObject(parameter).metaObjectForProperty(FlexConsts.ENTITY);
                Class<?> setterType = tableInfo.getReflector().getSetterType(idInfo.getProperty());
                Object id = ConvertUtil.convert(generateId, setterType);
                this.setValue(metaObject, this.idInfo.getProperty(), id);
            }*/
        } catch (Exception e) {
            throw new OrmException("");
        }
    }

    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {

    }
}