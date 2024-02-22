package cn.com.idmy.orm.core.table;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;

public final class EntityMetaObject {
    public static final ObjectFactory defaultObjectFactory = new DefaultObjectFactory();
    public static final ObjectWrapperFactory defaultObjectWrapperFactory = new DefaultObjectWrapperFactory();

    private EntityMetaObject() {
    }

    public static MetaObject forObject(Object object, ReflectorFactory reflectorFactory) {
        return MetaObject.forObject(object, defaultObjectFactory, defaultObjectWrapperFactory, reflectorFactory);
    }
}
