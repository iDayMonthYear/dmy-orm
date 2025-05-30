package cn.com.idmy.orm.util;

import cn.com.idmy.orm.core.Op;
import cn.com.idmy.orm.core.SqlNode;
import cn.com.idmy.orm.core.SqlNode.SqlColumn;
import cn.com.idmy.orm.core.SqlNode.SqlCond;
import cn.com.idmy.orm.core.Tables;
import cn.com.idmy.orm.core.Where;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.lang.tuple.Pair;
import org.dromara.hutool.core.lang.tuple.Triple;
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

    public static <T> void multiIdsAddEqNode(@NotNull Object id, Where<T, ?> where) {
        var entityType = where.entityType();
        var table = Tables.getTable(entityType);
        if (table.isMultiIds()) {
            var ids = table.ids();
            switch (id) {
                case Triple<?, ?, ?> triple -> {
                    where.addNode(new SqlCond(ids[0].name(), Op.EQ, triple.getLeft()));
                    where.addNode(new SqlCond(ids[1].name(), Op.EQ, triple.getMiddle()));
                    where.addNode(new SqlCond(ids[2].name(), Op.EQ, triple.getRight()));
                }
                case Pair<?, ?> pair -> {
                    where.addNode(new SqlCond(ids[0].name(), Op.EQ, pair.getLeft()));
                    where.addNode(new SqlCond(ids[1].name(), Op.EQ, pair.getRight()));
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
