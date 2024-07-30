package cn.com.idmy.orm.core.update;

import cn.com.idmy.orm.core.util.ClassUtil;
import cn.com.idmy.orm.core.util.MapUtil;
import org.apache.ibatis.javassist.util.proxy.ProxyFactory;
import org.apache.ibatis.javassist.util.proxy.ProxyObject;
import org.apache.ibatis.logging.LogFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author michael
 */
public class ModifyAttrsRecordProxyFactory {

    protected static final Map<Class<?>, Class<?>> CACHE = new ConcurrentHashMap<>();
    private static final ModifyAttrsRecordProxyFactory INSTANCE = new ModifyAttrsRecordProxyFactory();

    public static ModifyAttrsRecordProxyFactory getInstance() {
        return INSTANCE;
    }

    private ModifyAttrsRecordProxyFactory() {
    }

    public <T> T get(Class<T> target) {
        Class<?> proxyClass = MapUtil.computeIfAbsent(CACHE, target, aClass -> {
            ProxyFactory factory = new ProxyFactory();
            factory.setSuperclass(target);

            Class<?>[] interfaces = Arrays.copyOf(target.getInterfaces(), target.getInterfaces().length + 1);
            interfaces[interfaces.length - 1] = UpdateWrapper.class;
            factory.setInterfaces(interfaces);

            return factory.createClass();
        });

        T proxyObject = null;
        try {
            //noinspection unchecked
            proxyObject = (T) ClassUtil.newInstance(proxyClass);
            ((ProxyObject) proxyObject).setHandler(new ModifyAttrsRecordHandler());
        } catch (Exception e) {
            LogFactory.getLog(ModifyAttrsRecordProxyFactory.class).error(e.toString(), e);
        }

        return proxyObject;
    }
}



