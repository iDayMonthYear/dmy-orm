package cn.com.idmy.orm.core.query.ast;

public record From<T, CRUD extends Crud>(CRUD crud) {
    public From(CRUD crud) {
        this.crud = crud;
        crud.addNode(this);
    }

    public Where<T, CRUD> where() {
        return new Where<>(crud);
    }

    public CRUD semi() {
        return crud;
    }
}