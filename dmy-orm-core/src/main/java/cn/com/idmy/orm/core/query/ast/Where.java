package cn.com.idmy.orm.core.query.ast;

public class Where<T, CRUD extends Crud> extends AbstractCondition<T, CRUD> {
    public Where(CRUD crud) {
        super(crud);
    }
}
