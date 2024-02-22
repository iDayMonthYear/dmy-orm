package cn.com.idmy.orm.core.dialect;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DbType {
    MYSQL("mysql", "MySql 数据库"),
    OTHER("other", "其他数据库");
    private final String name;
    private final String remark;
}
