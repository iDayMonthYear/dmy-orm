package cn.com.idmy.orm.core;

/**
 * Mybatis-Flex 可能用到的静态常量
 *
 * @author michael
 * @author 王帅
 */
public interface OrmConsts {
    String NAME = "MyBatis-Flex";

    String SQL = "$$sql";
    String SQL_ARGS = "$$sql_args";
    String SCHEMA_NAME = "$$schemaName";
    String TABLE_NAME = "$$tableName";
    String FIELD_NAME = "$$fieldName";
    String PRIMARY_KEY = "$$primaryKey";
    String PRIMARY_VALUE = "$$primaryValue";
    String VALUE = "$$value";

    String QUERY = "$$query";
    String ROW = "$$row";
    String ROWS = "$$rows";

    String ENTITY = "$$entity";
    String ENTITIES = "$$entities";
    String IGNORE_NULLS = "$$ignoreNulls";

    String METHOD_INSERT_BATCH = "insertBatch";

    Object[] EMPTY_ARRAY = new Object[0];

    /**
     * 当 entity 使用逻辑删除时，0 为 entity 的正常状态
     */
    int LOGIC_DELETE_NORMAL = 0;
    /**
     * 当 entity 使用逻辑删除时，1 为 entity 的删除状态
     */
    int LOGIC_DELETE_DELETED = 1;
}
