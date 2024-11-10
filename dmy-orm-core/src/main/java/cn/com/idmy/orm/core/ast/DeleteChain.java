package cn.com.idmy.orm.core.ast;

import cn.com.idmy.orm.core.OrmDao;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;


@Getter
@Accessors(fluent = true, chain = false)
@Slf4j
public class DeleteChain<T> extends Sud<T, DeleteChain<T>> {
    private DeleteChain(Class<T> table) {
        super(table);
        sud = this;
    }

    public static <T> DeleteChain<T> of(OrmDao<T> dao) {
        return new DeleteChain<>(dao.entityType());
    }

    @Override
    protected String sql() {
        return DeleteSqlGenerator.gen(this);
    }

    @Override
    public String toString() {
        try {
            return sql();
        } catch (Exception e) {
            log.warn("SQL生成失败：{}", e.getMessage());
            return null;
        }
    }
}
