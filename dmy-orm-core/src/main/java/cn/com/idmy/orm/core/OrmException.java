package cn.com.idmy.orm.core;

public class OrmException extends RuntimeException {
    public OrmException(String e) {
        super(e);
    }

    public OrmException(Exception e) {
        super(e);
    }
}
