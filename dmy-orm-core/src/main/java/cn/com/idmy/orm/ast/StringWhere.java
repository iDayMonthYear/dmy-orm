package cn.com.idmy.orm.ast;

import cn.com.idmy.orm.ast.Node.Cond;


public interface StringWhere<T, WHERE extends StringWhere<T, WHERE>> {
    WHERE addNode(Node node);

    //region 等于
    default WHERE eq(String field, Object value) {
        return addNode(new Cond(field, Op.EQ, value));
    }
    //endregion

    //region 不等于
    default WHERE ne(String field, Object value) {
        return addNode(new Cond(field, Op.NE, value));
    }
    //endregion

    //region 大于 >
    default WHERE gt(String field, Object value) {
        return addNode(new Cond(field, Op.GT, value));
    }
    //endregion

    //region 大于等于 >=
    default WHERE ge(String field, Object value) {
        return addNode(new Cond(field, Op.GE, value));
    }

    //endregion

    //region 小于 <
    default WHERE lt(String field, Object value) {
        return addNode(new Cond(field, Op.LT, value));
    }

    //endregion

    //region 小于等于 <=
    default WHERE le(String field, Object value) {
        return addNode(new Cond(field, Op.LE, value));
    }

    //endregion

    //region in
    default WHERE in(String field, Object value) {
        return addNode(new Cond(field, Op.IN, value));
    }

    default WHERE in(String field, Object... values) {
        return addNode(new Cond(field, Op.IN, values));
    }
    //endregion
}