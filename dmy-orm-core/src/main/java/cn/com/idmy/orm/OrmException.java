package cn.com.idmy.orm;

import org.dromara.hutool.core.text.StrUtil;

public class OrmException extends IllegalArgumentException {
    public OrmException(String e) {
        super(e);
    }

    public OrmException(Exception e) {
        super(e);
    }

    public OrmException(String msg, Exception e) {
        super(msg, e);
    }

    public OrmException(String msg, Object... params) {
        super(StrUtil.format(msg, params));
    }
}
