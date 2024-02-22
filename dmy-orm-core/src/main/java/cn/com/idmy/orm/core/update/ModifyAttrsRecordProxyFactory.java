package cn.com.idmy.orm.core.update;

import cn.com.idmy.orm.core.util.ClassUtil;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.ibatis.javassist.util.proxy.ProxyFactory;
import org.apache.ibatis.javassist.util.proxy.ProxyObject;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.util.MapUtil;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author michael
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ModifyAttrsRecordProxyFactory {
    private static final Map<Class<?>, Class<?>> cache = new ConcurrentHashMap<>();
    @Getter
    private static final ModifyAttrsRecordProxyFactory instance = new ModifyAttrsRecordProxyFactory();

    @Nullable
    public <T> T get(Class<T> target) {
        Class<?> proxyClass = MapUtil.computeIfAbsent(cache, target, aClass -> {
            ProxyFactory factory = new ProxyFactory();
            factory.setSuperclass(target);
            Class<?>[] interfaces = Arrays.copyOf(target.getInterfaces(), target.getInterfaces().length + 1);
            interfaces[interfaces.length - 1] = UpdateWrapper.class;
            factory.setInterfaces(interfaces);
            return factory.createClass();
        });

        try {
            T proxyObject = (T) ClassUtil.newInstance(proxyClass);
            ((ProxyObject) proxyObject).setHandler(new ModifyAttrsRecordHandler());
            return proxyObject;
        } catch (Exception e) {
            LogFactory.getLog(ModifyAttrsRecordProxyFactory.class).error(e.toString(), e);
            return null;
        }
    }
}



