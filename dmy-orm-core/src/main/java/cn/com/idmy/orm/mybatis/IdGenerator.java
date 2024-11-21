package cn.com.idmy.orm.mybatis;

public interface IdGenerator {
    Object generate(Object entity, String column);
}
