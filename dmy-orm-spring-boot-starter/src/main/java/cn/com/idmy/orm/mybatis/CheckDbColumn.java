package cn.com.idmy.orm.mybatis;

import cn.com.idmy.base.annotation.Table;
import cn.com.idmy.orm.core.OrmDao;
import cn.com.idmy.orm.core.TableInfo;
import cn.com.idmy.orm.core.Tables;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.dromara.hutool.core.lang.Console;
import org.dromara.hutool.core.reflect.ClassUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.springframework.context.ApplicationContext;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
class CheckDbColumn {
    private final ApplicationContext ctx;
    private final SqlSessionFactory sqlSessionFactory;
    private final List<String> errors = new ArrayList<>();

    @SneakyThrows
    public void scan() {
        scanDao();
    }

    private void scanDao() throws Exception {
        try (var conn = sqlSessionFactory.openSession().getConnection()) {
            var mappers = ctx.getBeanNamesForType(OrmDao.class);
            for (var mapper : mappers) {
                var mapperType = ctx.getType(mapper);
                var entityType = ClassUtil.getTypeArgument(mapperType);
                if (entityType.isAnnotationPresent(Table.class)) {
                    check(conn, entityType, Tables.getTable(entityType));
                } else {
                    assert mapperType != null;
                    errors.add(StrUtil.format("{} 的实体类没有 Table 注解", mapperType.getName()));
                }
            }
        }
        if (!errors.isEmpty()) {
            errors.forEach(Console::error);
            System.exit(0);
        }
    }

    private void check(Connection conn, Class<?> entityType, TableInfo table) throws Exception {
        var meta = conn.getMetaData();
        var resultSet = meta.getColumns(null, null, table.name(), null);
        var columns = new HashSet<>();
        if (resultSet.next()) {
            do {
                columns.add(resultSet.getString("COLUMN_NAME").toLowerCase());
            } while (resultSet.next());
        } else {
            errors.add("实体类【" + entityType.getName() + "】表名【" + table.name() + "】在数据库中不存在");
            return;
        }

        for (var columnInfo : table.columns()) {
            if (!columns.contains(columnInfo.name().toLowerCase())) {
                errors.add("实体类【" + entityType.getName() + "】中的字段【" + columnInfo.name() + "】在数据库表中不存在");
            }
        }
    }
}