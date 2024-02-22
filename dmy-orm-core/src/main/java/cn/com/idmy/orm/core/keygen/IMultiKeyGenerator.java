package cn.com.idmy.orm.core.keygen;

public interface IMultiKeyGenerator {

    /**
     * 是否需要数据库生成主键
     *
     * @return true 需要生成主键
     */
    boolean hasGeneratedKeys();

    /**
     * 数据库主键的列名
     *
     * @return 列名数组
     */
    String[] getKeyColumnNames();

}
