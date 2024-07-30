package cn.com.idmy.orm.core.mybatis;

public class MappedStatementTypes {

    private MappedStatementTypes() {
    }

    private static final ThreadLocal<Class<?>> currentTypeTL = new ThreadLocal<>();

    public static void setCurrentType(Class<?> type) {
        currentTypeTL.set(type);
    }

    public static Class<?> getCurrentType() {
        return currentTypeTL.get();
    }

    public static void clear() {
        currentTypeTL.remove();
    }

}
