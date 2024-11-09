package cn.com.idmy.orm.core.query.ast;

public record Condition<T, CRUD extends Crud>(CRUD crud, Op op, Object field, Object expr) {
    public Condition(CRUD crud, Op op, Object field, Object expr) {
        this.crud = crud;
        this.op = op;
        this.field = field;
        this.expr = expr;
        crud.addNode(this);
    }

    public And<T, CRUD> and() {
        return new And<>(crud);
    }

    public Or<T, CRUD> or() {
        return new Or<>(crud);
    }

    public CRUD semi() {
        return crud;
    }
}