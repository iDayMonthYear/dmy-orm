package cn.com.idmy.orm.processor.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 表详细信息。
 *
 * @author 王帅
 * @since 2023-07-13
 */
@Getter
@Setter
public class TableInfo {
    /**
     * 实体类全类名。
     */
    private String entityName;

    /**
     * 实体类简单类名。
     */
    private String entitySimpleName;

    /**
     * 实体类注释。
     */
    private String entityComment;

    /**
     * 表名称。
     */
    private String tableName;

    /**
     * Schema 模式。
     */
    private String schema;

    public void setTableName(String tableName) {
        int indexOf = tableName.indexOf(".");
        if (indexOf > 0) {
            if (schema == null || schema.trim().isEmpty()) {
                this.schema = tableName.substring(0, indexOf);
                this.tableName = tableName.substring(indexOf + 1);
            } else {
                this.tableName = tableName;
            }
        } else {
            this.tableName = tableName;
        }
    }
}
