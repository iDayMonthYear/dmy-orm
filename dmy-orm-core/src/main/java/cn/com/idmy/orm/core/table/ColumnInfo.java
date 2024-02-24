package cn.com.idmy.orm.core.table;

import cn.com.idmy.orm.core.OrmConfig;
import cn.com.idmy.orm.core.mask.CompositeMaskTypeHandler;
import cn.com.idmy.orm.core.mask.MaskTypeHandler;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.time.chrono.JapaneseDate;
import java.util.Date;

@Getter
@Setter
public class ColumnInfo {

    private static final Class<?>[] needGetTypeHandlerTypes = {
            Date.class, java.sql.Date.class, Time.class, Timestamp.class,
            Instant.class, LocalDate.class, LocalDateTime.class, LocalTime.class, OffsetDateTime.class, OffsetTime.class, ZonedDateTime.class,
            Year.class, Month.class, YearMonth.class, JapaneseDate.class,
            byte[].class, Byte[].class, Byte.class,
    };

    /**
     * 数据库列名。
     */
    protected String column;

    /**
     * 列的别名。
     */
    protected String[] alias;

    /**
     * java entity 定义的属性名称（field name）。
     */
    protected String property;

    /**
     * 属性类型。
     */
    protected Class<?> propertyType;

    /**
     * 该列对应的 jdbcType。
     */
    protected JdbcType jdbcType;

    /**
     * 自定义 TypeHandler。
     */
    protected TypeHandler<?> typeHandler;

    /**
     * 最终使用和构建出来的 typeHandler
     */
    protected TypeHandler<?> buildTypeHandler;

    /**
     * 数据脱敏类型。
     */
    protected String maskType;

    /**
     * 是否忽略
     */
    protected boolean ignore;


    public TypeHandler<?> buildTypeHandler(Configuration configuration) {

        if (buildTypeHandler != null) {
            return buildTypeHandler;
        }

        //脱敏规则配置
        else if (StrUtil.isNotBlank(maskType)) {
            if (typeHandler != null) {
                //noinspection unchecked
                buildTypeHandler = new CompositeMaskTypeHandler(maskType, (TypeHandler<Object>) typeHandler);
            } else {
                buildTypeHandler = new MaskTypeHandler(maskType);
            }
        }

        //用户自定义的 typeHandler
        else if (typeHandler != null) {
            buildTypeHandler = typeHandler;
        }

        //枚举
        else if (propertyType.isEnum() || ArrayUtil.contains(needGetTypeHandlerTypes, propertyType)) {
            if (configuration == null) {
                configuration = OrmConfig.getDefaultConfig().getConfiguration();
            }
            if (configuration != null) {
                buildTypeHandler = configuration.getTypeHandlerRegistry().getTypeHandler(propertyType);
            }
        }

        return buildTypeHandler;
    }
}
