package cn.com.idmy.orm.core.query.util;

import cn.com.idmy.orm.core.query.OrmDao;

public class OrmUtil {
    public static String tableName(OrmDao<?> dao) {
        return dao.entityType().getSimpleName();
    }
}
