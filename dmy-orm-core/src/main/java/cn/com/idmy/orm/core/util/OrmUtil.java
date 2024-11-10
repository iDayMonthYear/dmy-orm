package cn.com.idmy.orm.core.util;

import cn.com.idmy.orm.core.OrmDao;

public class OrmUtil {
    public static String tableName(OrmDao<?> dao) {
        return dao.entityType().getSimpleName();
    }
}
