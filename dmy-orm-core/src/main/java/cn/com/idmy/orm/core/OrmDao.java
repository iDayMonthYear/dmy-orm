package cn.com.idmy.orm.core;

import cn.com.idmy.orm.core.ast.DeleteChain;
import cn.com.idmy.orm.core.ast.SelectChain;
import cn.com.idmy.orm.core.ast.UpdateChain;
import cn.com.idmy.orm.core.provider.MybatisSqlProvider;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.dromara.hutool.core.reflect.TypeUtil;

import java.util.List;

public interface OrmDao<T> {
    default Class<T> entityType() {
        return (Class<T>) TypeUtil.getTypeArgument(getClass());
    }

    @SelectProvider(type = MybatisSqlProvider.class, method = "find")
    List<T> find(@Param("chain") SelectChain<T> chain);

    @SelectProvider(type = MybatisSqlProvider.class, method = "get")
    T get(@Param("chain") SelectChain<T> chain);

    @UpdateProvider(type = MybatisSqlProvider.class, method = "update")
    int update(@Param("chain") UpdateChain<T> chain);

    @DeleteProvider(type = MybatisSqlProvider.class, method = "delete")
    int delete(@Param("chain") DeleteChain<T> chain);
}