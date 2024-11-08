package cn.com.idmy.orm.core.query.ast;


import org.dromara.hutool.core.text.StrUtil;

public class Count implements SelectItem {
    String alias;

    public Count() {
    }

    public Count(String alias) {
        this.alias = alias;
    }

    @Override
    public String toString() {
        return StrUtil.format(" count(*) {}", alias == null ? "" : "as " + alias);
    }
}
