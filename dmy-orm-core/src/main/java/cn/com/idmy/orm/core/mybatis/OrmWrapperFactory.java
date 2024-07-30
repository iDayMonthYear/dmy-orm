package cn.com.idmy.orm.core.mybatis;

import cn.com.idmy.orm.core.row.Row;
import cn.com.idmy.orm.core.table.TableInfo;
import cn.com.idmy.orm.core.table.TableInfoFactory;
import cn.com.idmy.orm.core.util.StringUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.reflection.wrapper.BeanWrapper;
import org.apache.ibatis.reflection.wrapper.MapWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;

import java.util.Collection;
import java.util.Map;

/**
 * @author michael
 */
public class OrmWrapperFactory implements ObjectWrapperFactory {

    @Override
    public boolean hasWrapperFor(Object object) {
        Class<?> objectClass = object.getClass();
        if (Collection.class.isAssignableFrom(objectClass)) {
            return false;
        } else if (Map.class.isAssignableFrom(objectClass)) {
            return true;
        }
        return TableInfoFactory.ofEntityClass(objectClass) != null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
        if (Map.class.isAssignableFrom(object.getClass())) {
            if (object.getClass() == Row.class) {
                //取消 row 的 user_name 转换为 userName，否则再次保存时无法进行保存
                //https://github.com/mybatis-flex/mybatis-flex/issues/244
                return new MapWrapper(metaObject, (Map<String, Object>) object);
            }
            return new FlexMapWrapper(metaObject, (Map<String, Object>) object);
        } else {
            return new FlexBeanWrapper(metaObject, object);
        }
    }

    static class FlexBeanWrapper extends BeanWrapper {

        private final Object entity;
        private final TableInfo tableInfo;

        public FlexBeanWrapper(MetaObject metaObject, Object object) {
            super(metaObject, object);
            this.entity = object;
            this.tableInfo = TableInfoFactory.ofEntityClass(object.getClass());
        }

        @Override
        public void set(PropertyTokenizer prop, Object value) {
            Object v = tableInfo.invokeOnSetListener(entity, prop.getName(), value);
            super.set(prop, v);
        }
    }


    static class FlexMapWrapper extends MapWrapper {

        public FlexMapWrapper(MetaObject metaObject, Map<String, Object> map) {
            super(metaObject, map);
        }

        @Override
        public String findProperty(String name, boolean useCamelCaseMapping) {
            return useCamelCaseMapping && (Character.isUpperCase(name.charAt(0)) || name.contains("_")) ? StringUtil.underlineToCamel(name) : name;
        }
    }

}
