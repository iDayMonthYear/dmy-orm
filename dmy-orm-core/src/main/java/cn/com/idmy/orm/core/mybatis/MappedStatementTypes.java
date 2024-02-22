package cn.com.idmy.orm.core.mybatis;

public class MappedStatementTypes {

    private MappedStatementTypes() {
    }

    private static final ThreadLocal<Class<?>> currentType = new ThreadLocal<>();

    public static void setCurrentType(Class<?> type) {
        currentType.set(type);
    }

    public static Class<?> getCurrentType() {
        return currentType.get();
    }

    public static void clear() {
        currentType.remove();
    }

}
