package cn.com.idmy.orm.core.mybatis;

import cn.com.idmy.orm.core.BaseMapper;
import cn.com.idmy.orm.core.OrmConfig;
import cn.com.idmy.orm.core.exception.OrmExceptions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.util.MapUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 获取 {@link BaseMapper} 对象。
 *
 * @author michael
 * @author 王帅
 */
@SuppressWarnings("unchecked")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Mappers {
    private static final Map<Class<?>, Object> mapperObjects = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Class<?>> entityMappers = new ConcurrentHashMap<>();

    /**
     * 添加 实体类 与 {@link BaseMapper} 接口实现接口 对应，两者皆为非动态代理类。
     *
     * @param entityClass 实体类
     * @param mapperClass {@link BaseMapper} 实现接口
     */
    static void addMapping(Class<?> entityClass, Class<?> mapperClass) {
        entityMappers.put(entityClass, mapperClass);
    }

    /**
     * 通过 实体类 获取对应 {@link BaseMapper} 对象。
     *
     * @param entityClass 实体类
     * @param <E>         实体类类型
     * @return {@link BaseMapper} 对象
     */
    public static <E> BaseMapper<E> ofEntityClass(Class<E> entityClass) {
        Class<?> mapperClass = entityMappers.get(entityClass);
        if (mapperClass == null) {
            throw OrmExceptions.wrap("Can not find MapperClass by entity: " + entityClass.getName());
        } else {
            return (BaseMapper<E>) ofMapperClass(mapperClass);
        }
    }

    /**
     * 通过 {@link BaseMapper} 接口实现的 Class 引用直接获取 {@link BaseMapper} 代理对象。
     *
     * @param mapperClass {@link BaseMapper} 接口实现
     * @return {@link BaseMapper} 对象
     */
    public static <M> M ofMapperClass(Class<M> mapperClass) {
        Object mapperObject = MapUtil.computeIfAbsent(mapperObjects, mapperClass, clazz ->
                Proxy.newProxyInstance(mapperClass.getClassLoader()
                        , new Class[]{mapperClass}
                        , new MapperHandler(mapperClass)));
        return (M) mapperObject;
    }

    private static class MapperHandler implements InvocationHandler {
        private final Class<?> mapperClass;
        private final ExecutorType executorType;
        private final SqlSessionFactory sqlSessionFactory;

        public MapperHandler(Class<?> mapperClass) {
            this.mapperClass = mapperClass;
            this.executorType = OrmConfig.getDefaultConfig().getConfiguration().getDefaultExecutorType();
            this.sqlSessionFactory = OrmConfig.getDefaultConfig().getSqlSessionFactory();
        }

        private SqlSession openSession() {
            return sqlSessionFactory.openSession(executorType, true);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try (SqlSession sqlSession = openSession()) {
                Object mapper = sqlSession.getMapper(mapperClass);
                return method.invoke(mapper, args);
            } catch (Throwable e) {
                throw ExceptionUtil.unwrapThrowable(e);
            }
        }
    }
}
