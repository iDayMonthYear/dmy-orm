package cn.com.idmy.ts.server.config;

import cn.com.idmy.orm.core.CrudInterceptor;
import cn.com.idmy.orm.core.CrudType;
import cn.com.idmy.orm.core.Op;
import cn.com.idmy.orm.core.SqlNode;
import cn.com.idmy.orm.core.SqlNode.SqlCond;
import jakarta.validation.constraints.NotNull;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

// 只关心查询操作的拦截器
public class QueryInterceptor implements CrudInterceptor {
    @Override
    public void beforeQuery(@NotNull Class<?> entityType, @NotNull List<SqlNode> nodes) {
        nodes.add(new SqlCond("key", Op.EQ, 1));
    }

    @Override
    public @NotNull Set<CrudType> interceptTypes() {
        return EnumSet.of(CrudType.SELECT);
    }
}
