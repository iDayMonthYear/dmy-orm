package cn.com.idmy.orm.core.query;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@RequiredArgsConstructor(staticName = "of")
@Accessors(fluent = true)
public class Column {
    protected final String name;
    protected String alias;
}