package cn.com.idmy.orm.core.dialect;

import cn.com.idmy.orm.core.query.QueryWrapper;

import static cn.com.idmy.orm.core.constant.SqlConsts.DELIMITER;
import static cn.com.idmy.orm.core.constant.SqlConsts.LIMIT;

/**
 * limit 和 offset 参数的处理器
 */
public interface LimitOffsetProcessor {
    /**
     * 处理构建 limit 和 offset
     *
     * @param dialect      数据方言
     * @param sql          已经构建的 sql
     * @param queryWrapper 参数内容
     * @param limitRows    用户传入的 limit 参数 可能为 null
     * @param limitOffset  用户传入的 offset 参数，可能为 null
     */
    StringBuilder process(Dialect dialect, StringBuilder sql, QueryWrapper queryWrapper, Long limitRows, Long limitOffset);

    /**
     * MySql 的处理器
     * 适合 {@link DbType#MYSQL,DbType#MARIADB,DbType#H2,DbType#CLICK_HOUSE,DbType#XCloud}
     */
    LimitOffsetProcessor MYSQL = (dialect, sql, queryWrapper, limitRows, limitOffset) -> {
        if (limitRows != null && limitOffset != null) {
            sql.append(LIMIT).append(limitOffset).append(DELIMITER).append(limitRows);
        } else if (limitRows != null) {
            sql.append(LIMIT).append(limitRows);
        }
        return sql;
    };
}
