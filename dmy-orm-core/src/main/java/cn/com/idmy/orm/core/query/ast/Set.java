package cn.com.idmy.orm.core.query.ast;

public record Set<T, CRUD extends Crud>(CRUD crud, Object field, Object expr) {
    public Set(CRUD crud, Object field, Object expr) {
        this.crud = crud;
        this.field = field;
        this.expr = expr;
        crud.addNode(this);
    }

    public Set<T, CRUD> set(String field, Object expr) {
        return new Set<>(crud, field, expr);
    }

    public Set<T, CRUD> set(FieldGetter<T, ?> field, Object expr) {
        return new Set<>(crud, field, expr);
    }

    public Set<T, CRUD> set(FieldGetter<T, ?> field, SqlExpression expr) {
        return new Set<>(crud, field, expr);
    }

    public Where<T, CRUD> where() {
        return new Where<>(crud);
    }

    public CRUD semi() {
        return crud;
    }
}