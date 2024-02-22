package cn.com.idmy.orm.core.datasource;

@FunctionalInterface
public interface DataSourceDecipher {
    String decrypt(DataSourceProperty property, String value);
}
