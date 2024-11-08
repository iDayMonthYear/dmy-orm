package cn.com.idmy.orm.core.query.ast;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class As {
    private String alias;
}
