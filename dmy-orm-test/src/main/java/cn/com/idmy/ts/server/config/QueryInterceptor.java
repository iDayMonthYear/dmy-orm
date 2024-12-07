package cn.com.idmy.ts.server.config;

import cn.com.idmy.orm.core.CrudInterceptor;
import cn.com.idmy.orm.core.CrudType;
import cn.com.idmy.orm.core.Op;
import cn.com.idmy.orm.core.SqlNode;
import cn.com.idmy.orm.core.SqlNode.SqlCond;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

// 只关心查询操作的拦截器
public class QueryInterceptor implements CrudInterceptor {
    @Override
    public void beforeSelect(Class<?> entityClass, List<SqlNode> nodes) {
        nodes.add(new SqlCond("key", Op.EQ, 1));
    }

    @Override
    public Set<CrudType> getInterceptTypes() {
        return EnumSet.of(CrudType.SELECT);
    }
}
