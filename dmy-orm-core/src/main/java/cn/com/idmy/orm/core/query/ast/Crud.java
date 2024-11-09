package cn.com.idmy.orm.core.query.ast;

public interface Crud {
    RootNode root();

    void addNode(Object node);
}
