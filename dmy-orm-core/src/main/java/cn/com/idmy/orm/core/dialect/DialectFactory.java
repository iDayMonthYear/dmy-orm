package cn.com.idmy.orm.core.dialect;


import cn.com.idmy.orm.core.OrmGlobalConfig;
import cn.com.idmy.orm.core.dialect.impl.CommonDialectImpl;
import cn.com.idmy.orm.core.util.MapUtil;
import cn.com.idmy.orm.core.util.ObjectUtil;

import java.util.EnumMap;
import java.util.Map;

/**
 * 方言工厂类，用于创建方言
 */
public class DialectFactory {

    private DialectFactory() {
    }

    /**
     * 数据库类型和方言的映射关系，可以通过其读取指定的方言，亦可能通过其扩展其他方言
     * 比如，在 mybatis-flex 实现的方言中有 bug 或者 有自己的独立实现，可以添加自己的方言实现到
     * 此 map 中，用于覆盖系统的方言实现
     */
    private static final Map<DbType, Dialect> dialectMap = new EnumMap<>(DbType.class);
    /**
     * 通过设置当前线程的数据库类型，以达到在代码执行时随时切换方言的功能
     */
    private static final ThreadLocal<DbType> dbTypeThreadLocal = new ThreadLocal<>();

    /**
     * 获取方言
     *
     * @return IDialect
     */
    public static Dialect getDialect() {
        DbType dbType = ObjectUtil.requireNonNullElse(dbTypeThreadLocal.get(),
                OrmGlobalConfig.getDefaultConfig().getDbType());
        return MapUtil.computeIfAbsent(dialectMap, dbType, DialectFactory::createDialect);
    }

    /**
     * 设置当前线程的 dbType
     */
    public static void setHintDbType(DbType dbType) {
        dbTypeThreadLocal.set(dbType);
    }

    /**
     * 获取当前线程的 dbType
     *
     * @return dbType
     */
    public static DbType getHintDbType() {
        return dbTypeThreadLocal.get();
    }

    /**
     * 清除当前线程的 dbType
     */
    public static void clearHintDbType() {
        dbTypeThreadLocal.remove();
    }

    /**
     * 可以为某个 dbType 注册（新增或覆盖）自己的方言
     *
     * @param dbType  数据库类型
     * @param dialect 方言的实现
     */
    public static void registerDialect(DbType dbType, Dialect dialect) {
        dialectMap.put(dbType, dialect);
    }

    private static Dialect createDialect(DbType dbType) {
        return switch (dbType) {
            case MYSQL, H2, LEALONE -> new CommonDialectImpl(KeywordWrap.BACK_QUOTE, LimitOffsetProcessor.MYSQL);
            default -> new CommonDialectImpl();
        };
    }

}
