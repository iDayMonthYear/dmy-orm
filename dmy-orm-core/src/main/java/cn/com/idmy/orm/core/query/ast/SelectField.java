package cn.com.idmy.orm.core.query.ast;

public record SelectField<T, CRUD extends Crud>(CRUD crud, Object expr) {
    public SelectField(CRUD crud, Object expr) {
        this.crud = crud;
        this.expr = expr;
        crud.addNode(this);
    }

    public From<T, CRUD> from() {
        return new From<>(crud);
    }
}