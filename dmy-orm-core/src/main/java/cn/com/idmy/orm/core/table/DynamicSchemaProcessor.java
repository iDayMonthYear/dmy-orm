package cn.com.idmy.orm.core.table;

import cn.com.idmy.orm.core.dialect.OperateType;

public interface DynamicSchemaProcessor {

    String process(String schema, String table, OperateType operateType);

}
