package cn.com.idmy.orm.core.table;

import org.apache.ibatis.reflection.ReflectorFactory;

public abstract class BaseReflectorFactory implements ReflectorFactory {
    @Override
    public boolean isClassCacheEnabled() {
        return true;
    }

    @Override
    public void setClassCacheEnabled(boolean classCacheEnabled) {
    }
}
