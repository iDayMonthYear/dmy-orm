package cn.com.idmy.orm;

public class OrmException extends IllegalArgumentException {
    public OrmException(String e) {
        super(e);
    }

    public OrmException(Exception e) {
        super(e);
    }

    public OrmException(String msg, Exception e) {
        super(msg, e);
    }
}
