package cn.com.idmy.orm.core.query.ast;

public class Where {
    public Eq eq(Exp exp) {
        return new Eq(exp);
    }
}
