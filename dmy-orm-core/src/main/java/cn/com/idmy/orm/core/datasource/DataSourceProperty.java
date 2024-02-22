package cn.com.idmy.orm.core.datasource;

import cn.com.idmy.orm.core.util.StringUtil;

public enum DataSourceProperty {
    URL("url", new String[]{"url", "jdbcUrl"}),
    USERNAME("username", new String[]{"username"}),
    PASSWORD("password", new String[]{"password"}),
    ;

    final String property;
    final String[] methods;

    DataSourceProperty(String property, String[] methods) {
        this.property = property;
        this.methods = methods;
    }

    String[] getGetterMethods() {
        String[] getterMethods = new String[methods.length];
        for (int i = 0; i < methods.length; i++) {
            getterMethods[i] = "get" + StringUtil.firstCharToUpperCase(methods[i]);
        }
        return getterMethods;
    }

    String[] getSetterMethods() {
        String[] getterMethods = new String[methods.length];
        for (int i = 0; i < methods.length; i++) {
            getterMethods[i] = "set" + StringUtil.firstCharToUpperCase(methods[i]);
        }
        return getterMethods;
    }
}
