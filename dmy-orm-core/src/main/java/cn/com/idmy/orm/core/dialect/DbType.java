package cn.com.idmy.orm.core.dialect;


import lombok.Getter;

public enum DbType {
    MYSQL("mysql", "MySql 数据库"),

    H2("h2", "H2 数据库"),

    LEALONE("lealone", "lealone 数据库"),

    /**
     * UNKNOWN DB
     */
    OTHER("other", "其他数据库");

    /**
     * 数据库名称
     */
    @Getter
    private final String name;

    /**
     * 描述
     */
    private final String remarks;

    DbType(String name, String remarks) {
        this.name = name;
        this.remarks = remarks;
    }
}
