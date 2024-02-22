package cn.com.idmy.orm.processor.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * 列详细信息。
 *
 * @author 王帅
 * @since 2023-07-01
 */
@Getter
@Setter
public class ColumnInfo implements Comparable<ColumnInfo> {

    /**
     * 属性名。
     */
    private String property;

    /**
     * 注释。
     */
    private String comment;

    /**
     * 列名。
     */
    private String column;

    /**
     * 别名。
     */
    private String[] alias;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ColumnInfo that = (ColumnInfo) o;
        return Objects.equals(property, that.property);
    }

    @Override
    public int hashCode() {
        return property != null ? property.hashCode() : 0;
    }

    @Override
    public int compareTo(ColumnInfo o) {
        // 先根据属性长度排序，属性名短的在上
        int compare = Integer.compare(property.length(), o.property.length());
        // 属性名长度一样，再按字母排序
        return compare == 0 ? property.compareTo(o.property) : compare;
    }
}
