package cn.com.idmy.orm.mybatis;

import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
class MybatisConsts {
    static final String CHAIN = "$chain$";
    static final String SQL_PARAMS = "$sqlParams$";

    static final String ENTITY = "$entity$";
    static final String ENTITIES = "$entities$";

    static final String GET = "get";
    static final String FIND = "find";
    static final String DELETE = "delete";
    static final String UPDATE = "update";
    static final String CREATE = "create";
    static final String CREATES = "creates";

    private static final String ENTITY_CLASS = "$$entityClass$";

    static void putEntityClass(Map<String, Object> params, Class<?> entityClass) {
        params.put(ENTITY_CLASS, entityClass);
    }

    static Class<?> getEntityClass(Map<String, Object> params) {
        return (Class<?>) params.get(ENTITY_CLASS);
    }

    static List<Object> findEntities(Map<String, Object> params) {
        return (List<Object>) params.get(ENTITIES);
    }
}
