package cn.com.idmy.orm.core.datasource;

public interface DataSourceDecipher {

    String decrypt(DataSourceProperty property, String value);

}
