package cn.com.idmy.orm.ast;

import cn.com.idmy.orm.ast.Node.SelectField;
import cn.com.idmy.orm.mybatis.MybatisDao;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Accessors(fluent = true, chain = false)
public class StringSelectChain<T> extends SelectChain<T> implements StringWhere<T, StringSelectChain<T>> {

    @Override
    public StringSelectChain<T> addNode(Node node) {
        super.addNode(node);
        return this;
    }

    protected StringSelectChain(Class<T> entity) {
        super(entity);
    }

    public static <T> StringSelectChain<T> of(MybatisDao<T, ?> dao) {
        return new StringSelectChain<>(dao.entityType());
    }

    public static <T> StringSelectChain<T> of(Class<T> entity) {
        return new StringSelectChain<>(entity);
    }

    public StringSelectChain<T> select(String... fields) {
        for (String f : fields) {
            addNode(new SelectField(f));
        }
        return this;
    }
}
