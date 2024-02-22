package cn.com.idmy.orm.core.update;

import cn.com.idmy.orm.core.util.FieldWrapper;
import cn.com.idmy.orm.core.util.StringUtil;
import lombok.Getter;
import org.apache.ibatis.javassist.util.proxy.MethodHandler;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
class ModifyAttrsRecordHandler implements MethodHandler {
    /**
     * 更新的字段和内容
     */
    private final Map<String, Object> updates = new LinkedHashMap<>();

    @Override
    public Object invoke(Object self, Method originalMethod, Method proxyMethod, Object[] args) throws Throwable {
        String methodName = originalMethod.getName();
        if (methodName.startsWith("set")
                && methodName.length() > 3
                && Character.isUpperCase(methodName.charAt(3))
                && originalMethod.getParameterCount() == 1) {

            String property = StringUtil.firstCharToLowerCase(originalMethod.getName().substring(3));

            //标识 @Column(ignore=true) 的字段，不去更新
            FieldWrapper fw = FieldWrapper.of(originalMethod.getDeclaringClass(), property);
            if (fw != null && fw.isIgnore()) {
                return proxyMethod.invoke(self, args);
            }

            updates.put(property, args[0]);
        }

        return proxyMethod.invoke(self, args);
    }
}
