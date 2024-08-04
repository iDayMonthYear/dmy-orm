package cn.com.idmy.orm.core.dialect;


import lombok.Getter;

public enum DbType {
    MYSQL("mysql", "MySql 数据库"),
    H2("h2", "H2 数据库"),
    LEALONE("lealone", "lealone 数据库"),
    OTHER("other", "其他数据库");

    @Getter
    private final String name;

    /**
     * 描述
     */
    private final String remark;

    DbType(String name, String remark) {
        this.name = name;
        this.remark = remark;
    }
}
