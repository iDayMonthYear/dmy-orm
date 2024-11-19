package cn.com.idmy.orm.ast;

import cn.com.idmy.base.model.Pair;
import cn.com.idmy.orm.mybatis.MybatisDao;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Getter
@Accessors(fluent = true, chain = false)
@Slf4j
public class DeleteChain<T> extends LambdaWhere<T, DeleteChain<T>> {
    private DeleteChain(Class<T> table) {
        super(table);
    }

    public static <T> DeleteChain<T> of(MybatisDao<T, ?> dao) {
        return new DeleteChain<>(dao.entityType());
    }

    @Override
    public Pair<String, List<Object>> sql() {
        return DeleteSqlGenerator.gen(this);
    }
}