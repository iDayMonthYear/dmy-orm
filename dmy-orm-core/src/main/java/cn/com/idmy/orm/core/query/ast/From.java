package cn.com.idmy.orm.core.query.ast;

public class From {
    public Where where() {
        return new Where();
    }

    public As as(String alias) {
        return new As().alias(alias);
    }
}
