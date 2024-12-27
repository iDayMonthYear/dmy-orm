package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.MybatisSqlProvider;
import cn.com.idmy.orm.mybatis.handler.TypeHandlerValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
class MybatisParameterHandler extends DefaultParameterHandler {
    @NotNull
    private final TypeHandlerRegistry typeHandlerRegistry;

    public MybatisParameterHandler(@NotNull MappedStatement ms, @NotNull Object param, @NotNull BoundSql boundSql) {
        super(ms, param, boundSql);
        this.typeHandlerRegistry = ms.getConfiguration().getTypeHandlerRegistry();
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public void setParameters(@NotNull PreparedStatement ps) {
        try {
            var params = (Map<String, Object>) getParameterObject();
            var sqlParams = (List<Object>) params.get(MybatisSqlProvider.SQL_PARAMS);
            if (sqlParams == null) {
                super.setParameters(ps);
            } else {
                for (int i = 0, size = sqlParams.size(); i < size; i++) {
                    setParameter(ps, i + 1, sqlParams.get(i), params);
                }
            }
        } catch (SQLException e) {
            throw new OrmException(e);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void setParameter(@NotNull PreparedStatement ps, int idx, @Nullable Object value, @NotNull Map<String, Object> params) throws SQLException {
        switch (value) {
            case null -> ps.setObject(idx, null);
            case Object[] val -> {
                if (val.length == 0) {
                    throw new OrmException("Empty array");
                }
                for (var item : val) {
                    setParameter(ps, idx++, item, params);
                }
            }
            case Collection<?> val -> {
                if (val.isEmpty()) {
                    throw new OrmException("Empty list");
                }
                for (var item : val) {
                    setParameter(ps, idx++, item, params);
                }
            }
            case TypeHandlerValue val -> val.setParameter(ps, idx);
            default -> {
                TypeHandler th = typeHandlerRegistry.getTypeHandler(value.getClass());
                if (th == null) {
                    th = typeHandlerRegistry.getUnknownTypeHandler();
                }
                th.setParameter(ps, idx, value, null);
            }
        }
    }
}