package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.OrmException;
import cn.com.idmy.orm.core.SqlProvider;
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
    private final @NotNull TypeHandlerRegistry typeHandlerRegistry;

    public MybatisParameterHandler(@NotNull MappedStatement ms, @NotNull Object param, @NotNull BoundSql boundSql) {
        super(ms, param, boundSql);
        this.typeHandlerRegistry = ms.getConfiguration().getTypeHandlerRegistry();
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public void setParameters(@NotNull PreparedStatement ps) {
        try {
            var params = (Map<String, Object>) getParameterObject();
            var sqlParams = (List<Object>) params.get(SqlProvider.SQL_PARAMS);
            if (sqlParams == null) {
                super.setParameters(ps);
            } else {
                int idx = 1;
                for (int i = 0, size = sqlParams.size(); i < size; i++) {
                    idx = setParameter(ps, idx, sqlParams.get(i), params);
                }
            }
        } catch (SQLException e) {
            throw new OrmException("设置参数异常", e);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private int setParameter(@NotNull PreparedStatement ps, int idx, @Nullable Object value, @NotNull Map<String, Object> params) throws SQLException {
        switch (value) {
            case null -> {
                ps.setObject(idx, null);
                return idx + 1;
            }
            case Object[] arr -> {
                if (arr.length == 0) {
                    throw new OrmException("空数组");
                }
                int curIdx = idx;
                for (int i = 0, len = arr.length; i < len; i++) {
                    curIdx = setParameter(ps, curIdx, arr[i], params);
                }
                return curIdx;
            }
            case Collection<?> arr -> {
                if (arr.isEmpty()) {
                    throw new OrmException("空集合");
                }
                int curIdx = idx;
                for (var item : arr) {
                    curIdx = setParameter(ps, curIdx, item, params);
                }
                return curIdx;
            }
            case TypeHandlerValue val -> {
                val.setParameter(ps, idx);
                return idx + 1;
            }
            default -> {
                TypeHandler th = typeHandlerRegistry.getTypeHandler(value.getClass());
                if (th == null) {
                    th = typeHandlerRegistry.getUnknownTypeHandler();
                }
                th.setParameter(ps, idx, value, null);
                return idx + 1;
            }
        }
    }
}