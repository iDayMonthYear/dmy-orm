package cn.com.idmy.orm.core;

import cn.com.idmy.orm.core.Node.Cond;


public interface StringWhere<T, WHERE extends StringWhere<T, WHERE>> {
    WHERE addNode(Node node);

    //region 等于
    default WHERE eq(String col, Object val) {
        return addNode(new Cond(col, Op.EQ, val));
    }
    //endregion

    //region 不等于
    default WHERE ne(String col, Object val) {
        return addNode(new Cond(col, Op.NE, val));
    }
    //endregion

    //region 大于 >
    default WHERE gt(String col, Object val) {
        return addNode(new Cond(col, Op.GT, val));
    }
    //endregion

    //region 大于等于 >=
    default WHERE ge(String col, Object val) {
        return addNode(new Cond(col, Op.GE, val));
    }

    //endregion

    //region 小于 <
    default WHERE lt(String col, Object val) {
        return addNode(new Cond(col, Op.LT, val));
    }

    //endregion

    //region 小于等于 <=
    default WHERE le(String col, Object val) {
        return addNode(new Cond(col, Op.LE, val));
    }

    //endregion

    //region in
    default WHERE in(String col, Object val) {
        return addNode(new Cond(col, Op.IN, val));
    }

    default WHERE in(String col, Object... vals) {
        return addNode(new Cond(col, Op.IN, vals));
    }
    //endregion
}