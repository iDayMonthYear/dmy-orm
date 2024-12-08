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
        var tableId = table.id();
        var idType = tableId.idType();
        if (idType == null || idType == IdType.NONE) {
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
}
