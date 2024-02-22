package cn.com.idmy.orm.core.dialect;


import cn.com.idmy.orm.core.OrmConfig;
import cn.com.idmy.orm.core.dialect.impl.DefaultDialectImpl;
import cn.com.idmy.orm.core.exception.OrmException;
import cn.com.idmy.orm.core.util.ObjectUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.ibatis.util.MapUtil;

import java.util.EnumMap;
import java.util.Map;

/**
 * 方言工厂类，用于创建方言
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DialectFactory {
    /**
     * 数据库类型和方言的映射关系，可以通过其读取指定的方言，亦可能通过其扩展其他方言
     * 比如，在 mybatis-flex 实现的方言中有 bug 或者 有自己的独立实现，可以添加自己的方言实现到
     * 此 map 中，用于覆盖系统的方言实现
     */
    private static final Map<DbType, Dialect> dialects = new EnumMap<>(DbType.class);

    /**
     * 通过设置当前线程的数据库类型，以达到在代码执行时随时切换方言的功能
     */
    private static final ThreadLocal<DbType> dbTypeTl = new ThreadLocal<>();
    private static DbType dbTypeGlobal = null;

    /**
     * 获取方言
     *
     * @return IDialect
     */
    public static Dialect getDialect() {
        DbType dbType = ObjectUtil.requireNonNullElse(dbTypeTl.get(), OrmConfig.getDefaultConfig().getDbType());
        return MapUtil.computeIfAbsent(dialects, dbType, DialectFactory::createDialect);
    }

    /**
     * 设置当前线程的 dbType
     *
     * @param dbType
     */
    public static void setHintDbType(DbType dbType) {
        dbTypeTl.set(dbType);
    }

    /**
     * 获取当前线程的 dbType
     *
     * @return dbType
     */
    public static DbType getHintDbType() {
        return dbTypeTl.get();
    }

    public static DbType getGlobalDbType() {
        return dbTypeGlobal;
    }

    public static void setGlobalDbType(DbType dbType) {
        if (dbTypeGlobal == null && dbType != null) {
            dbTypeGlobal = dbType;
        } else if (dbTypeGlobal != null) {
            throw new OrmException("dbTypeGlobal is only set once");
        } else {
            throw new OrmException("dbType can not be null");
        }
    }

    /**
     * 清除当前线程的 dbType
     */
    public static void clearHintDbType() {
        dbTypeTl.remove();
    }


    /**
     * 可以为某个 dbType 注册（新增或覆盖）自己的方言
     *
     * @param dbType  数据库类型
     * @param dialect 方言的实现
     */
    public static void registerDialect(DbType dbType, Dialect dialect) {
        dialects.put(dbType, dialect);
    }

    private static Dialect createDialect(DbType dbType) {
        switch (dbType) {
            case MYSQL:
                return new DefaultDialectImpl(KeywordWrap.BACK_QUOTE, LimitOffsetProcessor.MYSQL);
            default:
                return new DefaultDialectImpl();
        }
    }
}
