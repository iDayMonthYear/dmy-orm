package cn.com.idmy.orm.core.query.ast;

import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.hutool.core.text.StrUtil;

@Data
@Accessors(fluent = true)
public class Eq  {
    private Select root;
    private Object left;
    private Object right;
    private boolean or;

    public Eq(Select root, Object left, Object right) {
        this.root = root;
        this.left = left;
        this.right = right;
    }



    @Override
    public String toString() {
        return StrUtil.format(" {} = {} {}", left, right, or ? "or" : "and");
    }

    public String sql() {
        return root.toString();
    }
}
