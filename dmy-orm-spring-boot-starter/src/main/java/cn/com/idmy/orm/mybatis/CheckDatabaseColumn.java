package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.OrmConfig;
import cn.com.idmy.orm.annotation.Table;
import cn.com.idmy.orm.core.MybatisDao;
import cn.com.idmy.orm.core.TableInfo;
import cn.com.idmy.orm.core.Tables;
import jakarta.annotation.PostConstruct;
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
class CheckDatabaseColumn {
    private final ApplicationContext ctx;
    private final SqlSessionFactory sqlSessionFactory;
    private final List<String> errors = new ArrayList<>();

    @SneakyThrows
    @PostConstruct
    public void init() {
        scanMyBatisMappers();
    }

    private void scanMyBatisMappers() throws Exception {
        try (var connection = sqlSessionFactory.openSession().getConnection()) {
            var mapperBeanNames = ctx.getBeanNamesForType(MybatisDao.class);
            for (var beanName : mapperBeanNames) {
                var mapperClass = ctx.getType(beanName);
                var entityClass = ClassUtil.getTypeArgument(mapperClass);
                if (entityClass.isAnnotationPresent(Table.class)) {
                    var tableInfo = Tables.getTable(entityClass);
                    checkDatabaseColumn(connection, entityClass, tableInfo);
                } else {
                    assert mapperClass != null;
                    errors.add(StrUtil.format("{} 的实体类没有Table注解", mapperClass.getName()));
                }
            }
        }
        if (errors.isEmpty()) {
            Tables.clearTypeHandlers();
        } else {
            errors.forEach(Console::error);
            System.exit(0);
        }
    }

    private void checkDatabaseColumn(Connection connection, Class<?> entityClass, TableInfo tableInfo) throws Exception {
        var meta = connection.getMetaData();
        var resultSet = meta.getColumns(null, null, tableInfo.name(), null);
        var columns = new HashSet<>();
        if (resultSet.next()) {
            do {
                columns.add(resultSet.getString("COLUMN_NAME").toLowerCase());
            } while (resultSet.next());
        } else {
            errors.add("实体类【" + entityClass.getName() + "】表名【" + tableInfo.name() + "】在数据库中不存在");
            return;
        }

        for (var columnInfo : tableInfo.columns()) {
            if (!columns.contains(columnInfo.name().toLowerCase())) {
                errors.add("实体类【" + entityClass.getName() + "】中的字段【" + columnInfo.name() + "】在数据库表中不存在");
            }
        }
    }
}