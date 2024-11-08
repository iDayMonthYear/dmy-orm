package cn.com.idmy.orm.core.query.ast;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class Eq extends Op {
    private Exp exp;

    public Eq(Exp exp) {
        this.exp = exp;
    }
}
