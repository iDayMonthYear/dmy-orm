package cn.com.idmy.ts.server.config;

import cn.com.idmy.orm.core.CrudInterceptor;
import cn.com.idmy.orm.core.Node;
import cn.com.idmy.orm.core.Node.Cond;
import cn.com.idmy.orm.core.Op;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

// 只关心查询操作的拦截器
public class QueryInterceptor implements CrudInterceptor {
    @Override
    public void beforeSelect(Class<?> entityClass, List<Node> nodes) {
        nodes.add(new Cond("key", Op.EQ, 1));
    }

    @Override
    public Set<CrudType> getInterceptTypes() {
        return EnumSet.of(CrudType.SELECT);
    }
}
