package cn.com.idmy.orm.util;

import cn.com.idmy.base.FieldGetter;
import cn.com.idmy.base.model.Pair;
import cn.com.idmy.base.model.Triple;
import cn.com.idmy.orm.core.Op;
import cn.com.idmy.orm.core.SqlNode;
import cn.com.idmy.orm.core.SqlNode.SqlColumn;
import cn.com.idmy.orm.core.SqlNode.SqlCond;
import cn.com.idmy.orm.core.Tables;
import cn.com.idmy.orm.core.Where;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.convert.ConvertUtil;
import org.dromara.hutool.core.reflect.ClassUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

@UtilityClass
@Slf4j
public class OrmUtil {
    public boolean hasColumn(List<SqlNode> nodes, @NotNull String column, @NotNull SqlNode.Type type) {
        return nodes.stream().anyMatch(n -> {
            if (n instanceof SqlColumn col) {
                return Objects.equals(col.column(), column) && n.type() == type;
            } else {
                return false;
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static @NotNull <T, R extends Number> R toZero(@NotNull FieldGetter<T, R> field) {
        var fieldType = ClassUtil.getTypeArgument(field.getClass());
        return (R) ConvertUtil.convert(fieldType, 0);
    }

    public static <T> void multiIdsAddEqNode(@NotNull Object id, Where<T, ?> where) {
        var entityType = where.entityType();
        var table = Tables.getTable(entityType);
        if (table.isMultiIds()) {
            var ids = table.ids();
            switch (id) {
                case Pair<?, ?> pair -> {
                    where.addNode(new SqlCond(ids[0].name(), Op.EQ, pair.l()));
                    where.addNode(new SqlCond(ids[1].name(), Op.EQ, pair.r()));
                }
                case Triple<?, ?, ?> triple -> {
                    where.addNode(new SqlCond(ids[0].name(), Op.EQ, triple.l()));
                    where.addNode(new SqlCond(ids[1].name(), Op.EQ, triple.m()));
                    where.addNode(new SqlCond(ids[2].name(), Op.EQ, triple.r()));
                }
                case Object[] arr -> {
                    for (int i = 0, len = ids.length; i < len; i++) {
                        var tmp = ids[i];
                        where.addNode(new SqlCond(tmp.name(), Op.EQ, arr[i]));
                    }
                }
                default -> {
                }
            }
        } else {
            where.addNode(new SqlCond(Tables.getIdColumnName(entityType), Op.EQ, id));
        }
    }
}
