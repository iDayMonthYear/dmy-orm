package cn.com.idmy.orm.core.datasource;

import cn.com.idmy.orm.core.util.StringUtil;

public enum DataSourceProperty {

    URL("url", new String[]{"url", "jdbcUrl"}),
    USERNAME("username", new String[]{"username"}),
    PASSWORD("password", new String[]{"password"}),
    ;

    final String property;
    final String[] methodFlags;

    DataSourceProperty(String property, String[] methodFlags) {
        this.property = property;
        this.methodFlags = methodFlags;
    }

    String[] getGetterMethods() {
        String[] getterMethods = new String[methodFlags.length];
        for (int i = 0; i < methodFlags.length; i++) {
            getterMethods[i] = "get" + StringUtil.firstCharToUpperCase(methodFlags[i]);
        }
        return getterMethods;
    }

    String[] getSetterMethods() {
        String[] getterMethods = new String[methodFlags.length];
        for (int i = 0; i < methodFlags.length; i++) {
            getterMethods[i] = "set" + StringUtil.firstCharToUpperCase(methodFlags[i]);
        }
        return getterMethods;
    }
}
