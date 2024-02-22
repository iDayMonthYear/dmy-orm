package cn.com.idmy.orm.core.row;

public interface BatchArgsSetter {

    int getBatchSize();

    Object[] getSqlArgs(int index);

}
