package cn.com.idmy.orm.core.query.ast;

public class And<T, CRUD extends Crud> extends AbstractCondition<T, CRUD> {
    public And(CRUD crud) {
        super(crud);
    }
}