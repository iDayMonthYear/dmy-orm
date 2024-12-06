package cn.com.idmy.orm.mybatis;

import cn.com.idmy.base.annotation.Table.Id.IdType;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.TableInfo;
import lombok.NoArgsConstructor;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.dromara.hutool.core.text.StrUtil;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class MybatisIdGeneratorUtil {
    public static KeyGenerator create(MappedStatement ms, TableInfo table) {
        var id = table.id();
        var type = id.idType();
        if (type == null || type == IdType.NONE) {
            return NoKeyGenerator.INSTANCE;
        } else if (type == IdType.AUTO) {
            return Jdbc3KeyGenerator.INSTANCE;
        } else if (type == IdType.GENERATOR) {
            return new CustomIdGenerator(ms.getConfiguration(), table);
        } else {
            var sequence = id.value();
            if (StrUtil.isBlank(sequence)) {
                throw new OrmException(StrUtil.format("Please config sequence by @Table.Id(value=\"...\") for field: {} in class: {}", id.name(), table.entityClass().getSimpleName()));
            } else {
                return MybatisModifier.getSelectKeyGenerator(ms, id);
            }
        }
    }

}
