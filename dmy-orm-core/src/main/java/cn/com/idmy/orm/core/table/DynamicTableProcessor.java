package cn.com.idmy.orm.core.table;

import cn.com.idmy.orm.core.dialect.OperateType;

public interface DynamicTableProcessor {
    String process(String tableName, OperateType operateType);
}
