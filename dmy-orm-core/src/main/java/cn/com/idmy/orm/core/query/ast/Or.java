package cn.com.idmy.orm.core.query.ast;

public class Or<T, CRUD extends Crud> extends AbstractCondition<T, CRUD> {
    public Or(CRUD crud) {
        super(crud);
    }
}