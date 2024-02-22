package cn.com.idmy.orm.core.update;

import cn.com.idmy.orm.core.table.IdInfo;
import cn.com.idmy.orm.core.table.TableInfo;
import cn.com.idmy.orm.core.table.TableInfoFactory;
import cn.com.idmy.orm.core.util.ClassUtil;
import cn.com.idmy.orm.core.util.ConvertUtil;
import cn.com.idmy.orm.core.util.Reflectors;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.reflection.invoker.Invoker;

import java.lang.reflect.Array;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateEntity {
    public static <T> T of(Class<T> clazz) {
        clazz = ClassUtil.getUsefulClass(clazz);
        return ModifyAttrsRecordProxyFactory.getInstance().get(clazz);
    }

    public static <T> T of(Class<T> clazz, Object id) {
        clazz = ClassUtil.getUsefulClass(clazz);
        T newEntity = ModifyAttrsRecordProxyFactory.getInstance().get(clazz);
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(clazz);
        List<IdInfo> primaryKeyList = tableInfo.getPrimaryKeyList();
        Reflector reflector = Reflectors.of(clazz);

        if (primaryKeyList != null && !primaryKeyList.isEmpty()) {
            for (int i = 0; i < primaryKeyList.size(); i++) {
                IdInfo idInfo = primaryKeyList.get(i);
                Object idValue = getIdValue(id, i);
                Invoker setInvoker = reflector.getSetInvoker(idInfo.getProperty());
                try {
                    setInvoker.invoke(newEntity, new Object[]{ConvertUtil.convert(idValue, idInfo.getPropertyType())});
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        return newEntity;
    }


    @Nullable
    private static Object getIdValue(Object id, int index) {
        if (id == null) {
            return null;
        }
        if (ClassUtil.isArray(id.getClass())) {
            if (index >= Array.getLength(id)) {
                return null;
            } else {
                return Array.get(id, index);
            }
        }
        //not array
        return index == 0 ? id : null;
    }


    public static <T> T ofNotNull(T entity) {
        Class<?> usefulClass = ClassUtil.getUsefulClass(entity.getClass());
        T newEntity = (T) of(usefulClass);

        Reflector reflector = Reflectors.of(usefulClass);
        String[] propertyNames = reflector.getGetablePropertyNames();

        for (String propertyName : propertyNames) {
            try {
                Object value = reflector.getGetInvoker(propertyName).invoke(entity, null);
                if (value != null) {
                    reflector.getSetInvoker(propertyName).invoke(newEntity, new Object[]{value});
                }
            } catch (Exception ignored) {
            }
        }
        return newEntity;
    }
}
