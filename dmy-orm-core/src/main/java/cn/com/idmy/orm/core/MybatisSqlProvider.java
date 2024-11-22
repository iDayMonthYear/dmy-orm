package cn.com.idmy.orm.core;

import cn.com.idmy.base.model.Page;
import cn.com.idmy.base.model.Param;
import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.Node.Cond;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.collection.CollStreamUtil;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.reflect.FieldUtil;

import java.util.*;

import static cn.com.idmy.orm.core.MybatisDao.DEFAULT_BATCH_SIZE;

@Slf4j
public class MybatisSqlProvider {
    public static final String CHAIN = "$chain$";
    public static final String SQL_PARAMS = "$sqlParams$";

    public static final String ENTITY = "$entity$";
    public static final String ENTITIES = "$entities$";

    public static final String GET = "get";
    public static final String FIND = "find";
    public static final String DELETE = "delete";
    public static final String UPDATE = "update";
    public static final String COUNT = "count";
    public static final String INSERT = "insert";
    public static final String INSERTS = "inserts";

    private static final String ENTITY_CLASS = "$$entityClass$";

    public static void putEntityClass(Map<String, Object> params, Class<?> entityClass) {
        params.put(ENTITY_CLASS, entityClass);
    }

    public static Class<?> getEntityClass(Map<String, Object> params) {
        return (Class<?>) params.get(ENTITY_CLASS);
    }

    public static List<Object> findEntities(Map<String, Object> params) {
        return (List<Object>) params.get(ENTITIES);
    }

    private static void clearSelects(SelectChain<?> chain) {
        if (chain.hasSelectColumn) {
            chain.clearSelects();
            log.warn("select ... from 中间不能有字段或者函数");
        }
    }

    private static String buildCommonSql(Map<String, Object> params) {
        var where = (AbstractWhere<?, ?>) params.get(CHAIN);
        putEntityClass(params, where.entityClass());
        var pair = where.sql();
        params.put(SQL_PARAMS, pair.right);
        return pair.left;
    }

    public String get(Map<String, Object> params) {
        return buildCommonSql(params);
    }

    public String find(Map<String, Object> params) {
        return buildCommonSql(params);
    }

    public String update(Map<String, Object> params) {
        return buildCommonSql(params);
    }

    public String delete(Map<String, Object> params) {
        return buildCommonSql(params);
    }

    public String count(Map<String, Object> params) {
        var chain = (SelectChain<?>) params.get(CHAIN);
        clearSelects(chain);
        chain.limit = null;
        chain.offset = null;
        chain.select(SqlFn::count);
        putEntityClass(params, chain.entityClass());
        var pair = chain.sql();
        params.put(SQL_PARAMS, pair.right);
        return pair.left;
    }

    public String insert(Map<String, Object> params) {
        var entity = params.get(ENTITY);
        var table = TableManager.getTableInfo(entity.getClass());
        var columns = table.columns();

        var sql = builderInsertHeader(table.name());

        var values = new StringBuilder(SqlConsts.VALUES).append(SqlConsts.BRACKET_LEFT);

        var sqlParams = new ArrayList<>(columns.length);

        for (int i = 0, size = columns.length; i < size; i++) {
            var column = columns[i];
            sql.append(SqlConsts.STRESS_MARK).append(column.name()).append(SqlConsts.STRESS_MARK);
            values.append(SqlConsts.PLACEHOLDER);

            sqlParams.add(FieldUtil.getFieldValue(entity, column.field()));

            if (i < size - 1) {
                sql.append(SqlConsts.DELIMITER);
                values.append(SqlConsts.DELIMITER);
            }
        }

        params.put(SQL_PARAMS, sqlParams);
        putEntityClass(params, entity.getClass());
        return sql.append(SqlConsts.BRACKET_RIGHT).append(values).append(SqlConsts.BRACKET_RIGHT).toString();
    }

    public String inserts(Map<String, Object> params) {
        var entities = findEntities(params);
        if (entities.isEmpty()) {
            throw new OrmException("批量插入的实体集合不能为空");
        }
        var table = TableManager.getTableInfo(entities.getFirst().getClass());
        var columns = table.columns();
        var sql = builderInsertHeader(table.name());

        // 构建列名部分
        for (int i = 0, size = columns.length; i < size; i++) {
            var column = columns[i];
            sql.append(SqlConsts.STRESS_MARK).append(column.name()).append(SqlConsts.STRESS_MARK);
            if (i < size - 1) {
                sql.append(SqlConsts.DELIMITER);
            }
        }
        sql.append(SqlConsts.BRACKET_RIGHT);

        // 构建values部分
        sql.append(SqlConsts.VALUES);
        var sqlParams = new ArrayList<>(columns.length);
        var first = true;
        for (var entity : entities) {
            if (first) {
                first = false;
            } else {
                sql.append(SqlConsts.DELIMITER);
            }
            sql.append(SqlConsts.BRACKET_LEFT);
            for (int i = 0, size = columns.length; i < size; i++) {
                sql.append(SqlConsts.PLACEHOLDER);
                sqlParams.add(FieldUtil.getFieldValue(entity, columns[i].name()));
                if (i < size - 1) {
                    sql.append(SqlConsts.DELIMITER);
                }
            }
            sql.append(SqlConsts.BRACKET_RIGHT);
        }
        params.put(SQL_PARAMS, sqlParams);
        putEntityClass(params, entities.getFirst().getClass());
        return sql.toString();
    }

    private static StringBuilder builderInsertHeader(String name) {
        return new StringBuilder(SqlConsts.INSERT_INTO)
                .append(SqlConsts.STRESS_MARK)
                .append(name)
                .append(SqlConsts.STRESS_MARK)
                .append(SqlConsts.BLANK)
                .append(SqlConsts.BRACKET_LEFT);
    }

    public static <T, ID> int inserts(MybatisDao<T, ID> dao, Collection<T> entities, int size) {
        if (entities.isEmpty()) {
            return 0;
        }
        if (size <= 0) {
            size = DEFAULT_BATCH_SIZE;
        }
        var entityList = entities instanceof List ? (List<T>) entities : new ArrayList<>(entities);
        int sum = 0;
        int entitiesSize = entities.size();
        int maxIndex = entitiesSize / size + (entitiesSize % size == 0 ? 0 : 1);
        for (int i = 0; i < maxIndex; i++) {
            sum += dao.inserts(entityList.subList(i * size, Math.min(i * size + size, entitiesSize)));
        }
        return sum;
    }

    @Nullable
    public static <T, ID> T get(MybatisDao<T, ID> dao, ID id) {
        var chain = StringSelectChain.of(dao);
        chain.sqlParamsSize(1);
        chain.eq(TableManager.getIdName(dao.entityClass()), id);
        return dao.get(chain);
    }

    @Nullable
    public static <T, ID, R> R get(MybatisDao<T, ID> dao, ColumnGetter<T, R> getter, ID id) {
        var chain = (StringSelectChain<T>) StringSelectChain.of(dao).select(getter);
        chain.sqlParamsSize(1);
        chain.eq(TableManager.getIdName(dao.entityClass()), id);
        T t = dao.get(chain);
        if (t == null) {
            return null;
        } else {
            return getter.get(t);
        }
    }

    public static <T, ID, R> R get(MybatisDao<T, ID> dao, ColumnGetter<T, R> getter, SelectChain<T> chain) {
        clearSelects(chain);
        chain.limit = 1;
        T t = dao.get(chain.select(getter));
        if (t == null) {
            return null;
        } else {
            return getter.get(t);
        }
    }

    public static <T, ID> T get(MybatisDao<T, ID> dao, SelectChain<T> chain, @NonNull ColumnGetter<T, ?>[] getters) {
        clearSelects(chain);
        chain.limit = 1;
        return dao.get(chain.select(getters));
    }

    public static <T, ID, R> List<R> find(MybatisDao<T, ID> dao, ColumnGetter<T, R> getter, SelectChain<T> chain) {
        clearSelects(chain);
        var ts = dao.find(chain.select(getter));
        return CollStreamUtil.toList(ts, getter::get);
    }

    public static <T, ID> List<T> find(MybatisDao<T, ID> dao, Collection<ID> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        } else {
            var chain = StringSelectChain.of(dao);
            chain.sqlParamsSize(1);
            chain.in(TableManager.getIdName(dao.entityClass()), ids);
            return dao.find(chain);
        }
    }

    public static <T, ID, R> List<R> find(MybatisDao<T, ID> dao, ColumnGetter<T, R> getter, Collection<ID> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        } else {
            var chain = (StringSelectChain<T>) StringSelectChain.of(dao).select(getter);
            chain.sqlParamsSize(1);
            chain.in(TableManager.getIdName(dao.entityClass()), ids);
            return dao.find(chain).stream().map(getter::get).toList();
        }
    }

    @Nullable
    public static <T, ID, R extends Number> R fn(MybatisDao<T, ID> dao, SqlFnName name, ColumnGetter<T, R> getter, SelectChain<T> chain) {
        if (name == SqlFnName.IF_NULL) {
            throw new IllegalArgumentException("不支持ifnull");
        } else {
            clearSelects(chain);
            chain.limit = 1;
            T t = dao.get(chain.select(() -> new SqlFn<>(name, getter)));
            if (t == null) {
                return null;
            } else {
                return getter.get(t);
            }
        }
    }

    public static <T, ID> boolean exists(MybatisDao<T, ID> dao, ID id) {
        var chain = StringSelectChain.of(dao);
        chain.sqlParamsSize(1);
        chain.eq(TableManager.getIdName(dao.entityClass()), id);
        return dao.count(chain) > 0;
    }

    public static <T, ID> int delete(MybatisDao<T, ID> dao, ID id) {
        var chain = StringDeleteChain.of(dao);
        chain.sqlParamsSize(1);
        chain.eq(TableManager.getIdName(dao.entityClass()), id);
        return dao.delete(chain);
    }

    public static <T, ID> int delete(MybatisDao<T, ID> dao, Collection<ID> ids) {
        if (ids.isEmpty()) {
            return 0;
        } else {
            var chain = StringDeleteChain.of(dao);
            chain.sqlParamsSize(1);
            chain.in(TableManager.getIdName(dao.entityClass()), ids);
            return dao.delete(chain);
        }
    }

    public static <T, ID> Map<ID, T> map(MybatisDao<T, ID> dao, @NonNull ID[] ids) {
        if (ids.length == 0) {
            return Collections.emptyMap();
        } else {
            var chain = StringSelectChain.of(dao);
            chain.sqlParamsSize(1);
            chain.in(TableManager.getIdName(dao.entityClass()), (Object) ids);
            var entities = dao.find(chain);
            return CollStreamUtil.toIdentityMap(entities, TableManager::getIdValue);
        }
    }

    public static <T, ID> Map<ID, T> map(MybatisDao<T, ID> dao, @NonNull Collection<ID> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        } else {
            var chain = StringSelectChain.of(dao);
            chain.sqlParamsSize(1);
            chain.in(TableManager.getIdName(dao.entityClass()), ids);
            var entities = dao.find(chain);
            return CollStreamUtil.toIdentityMap(entities, TableManager::getIdValue);
        }
    }

    public static <T, ID, R> Page<T> page(MybatisDao<T, ID> dao, Page<R> pageIn, SelectChain<T> select) {
        select.limit = pageIn.getPageSize();
        select.offset = pageIn.getOffset();
        select.orderBy(pageIn.getSorts());

        var params = pageIn.getParams();
        if (params instanceof Param<?> param) {
            var entityClass = dao.entityClass();
            var idVal = param.getId();
            if (idVal != null) {
                var idName = TableManager.getIdName(entityClass);
                select.addNode(new Cond(idName, Op.EQ, idVal));
            } else {
                var idsVal = param.getIds();
                if (CollUtil.isNotEmpty(idsVal)) {
                    var idName = TableManager.getIdName(entityClass);
                    select.addNode(new Cond(idName, Op.IN, idsVal));
                } else {
                    var notIdsVal = param.getIds();
                    if (CollUtil.isNotEmpty(idsVal)) {
                        var idName = TableManager.getIdName(entityClass);
                        select.addNode(new Cond(idName, Op.NOT_IN, notIdsVal));
                    }
                }
            }

            var createdAts = param.getCreatedAts();
            if (ArrayUtil.isNotEmpty(createdAts) && createdAts.length == 2) {
                String createdAt = TableManager.getColumnName(entityClass, "createdAt");
                select.addNode(new Cond(createdAt, Op.BETWEEN, createdAts));
            }
            var updatedAts = param.getUpdatedAts();
            if (ArrayUtil.isNotEmpty(updatedAts) && updatedAts.length == 2) {
                String updatedAt = TableManager.getColumnName(entityClass, "updatedAt");
                select.addNode(new Cond(updatedAt, Op.BETWEEN, createdAts));
            }
        }

        var rows = dao.find(select);
        if (pageIn.getNeedTotal() == null || pageIn.getNeedTotal()) {
            pageIn.setTotal(dao.count(select));
        } else {
            pageIn.setTotal(rows.size());
        }
        return Page.of(pageIn.getPageNo(), pageIn.getPageSize(), pageIn.getTotal(), rows);
    }
}