package cn.com.idmy.orm.core.keygen;

public interface IKeyGenerator {

    Object generate(Object entity, String keyColumn);

}
