package cn.com.idmy.orm.util;

import cn.com.idmy.orm.core.SqlNode;
import cn.com.idmy.orm.core.SqlNode.SqlColumn;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Objects;

@UtilityClass
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
}
