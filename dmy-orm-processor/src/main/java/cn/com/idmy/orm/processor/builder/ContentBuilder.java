package cn.com.idmy.orm.processor.builder;

import cn.com.idmy.orm.processor.entity.ColumnInfo;
import cn.com.idmy.orm.processor.entity.TableInfo;
import cn.com.idmy.orm.processor.util.StrUtil;

import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

/**
 * 文件内容构建。
 *
 * @author 王帅
 * @since 2023-06-23
 */
@SuppressWarnings({"squid:S107", "squid:S1192"})
public class ContentBuilder {

    private ContentBuilder() {
    }

    /**
     * 构建 Mapper 文件内容。
     */
    public static String buildMapper(TableInfo tableInfo, String mappersPackage, String mapperClassName, String baseMapperClass, boolean mapperAnnotationEnable) {
        String entityClass = tableInfo.getEntityName();
        StringBuilder content = new StringBuilder("package ");
        content.append(mappersPackage).append(";\n\n");
        content.append("import ").append(baseMapperClass).append(";\n");
        content.append("import ").append(entityClass).append(";\n\n");
        if (mapperAnnotationEnable) {
            content.append("import org.apache.ibatis.annotations.Mapper;\n\n");
            content.append("@Mapper\n");
        }
        String realEntityClassName = StrUtil.getClassName(entityClass);
        String baseMapperClassName = StrUtil.getClassName(baseMapperClass);
        content.append("public interface ").append(mapperClassName).append(" extends ").append(baseMapperClassName).append("<").append(realEntityClassName).append("> {\n}");
        return content.toString();
    }

    /**
     * 构建 TableDef 文件内容。
     */
    public static String buildTableDef(TableInfo tableInfo, boolean allInTablesEnable,
                                       String tableDefPackage, String tableDefClassName,
                                       String tableDefPropertiesNameStyle, String tableDefInstanceSuffix,
                                       Collection<ColumnInfo> columnInfos, List<String> defaultColumns) {
        StringBuilder content = new StringBuilder("package ");
        content.append(tableDefPackage).append(";\n\n");
        content.append("import cn.com.idmy.orm.core.query.QueryColumn;\n");
        content.append("import cn.com.idmy.orm.core.table.TableDef;\n\n");
        content.append("public class ").append(tableDefClassName).append(" extends TableDef {\n");

        //TableDef 类的属性名称
        String tableDefPropertyName = null;
        if (!allInTablesEnable) {
            tableDefPropertyName = StrUtil.buildFieldName(tableInfo.getEntitySimpleName().concat(tableDefInstanceSuffix != null ? tableDefInstanceSuffix.trim() : ""), "upperCase");
            content.append("    public static final ").append(tableDefClassName).append(' ').append(tableDefPropertyName)
                    .append(" = new ").append(tableDefClassName).append("();\n");
        }


        String finalTableDefPropertyName = tableDefPropertyName;
        columnInfos.forEach(columnInfo -> {
            // QueryColumn 属性定义的名称
            String columnPropertyName = StrUtil.buildFieldName(columnInfo.getProperty(), tableDefPropertiesNameStyle);

            //当字段名称和表名一样时，自动为字段添加一个小尾巴 "_"，例如 account_
            if (columnPropertyName.equals(finalTableDefPropertyName)) {
                columnPropertyName = columnPropertyName + "_";
            }
            content.append("    public final QueryColumn ")
                    .append(columnPropertyName)
                    .append(" = new QueryColumn(this, \"")
                    .append(columnInfo.getColumn()).append("\"");
            if (columnInfo.getAlias() != null && columnInfo.getAlias().length > 0) {
                content.append(", \"").append(columnInfo.getAlias()[0]).append("\"");
            }
            content.append(");\n");
        });
        content.append("    public final QueryColumn ").append(StrUtil.buildFieldName("allColumns", tableDefPropertiesNameStyle)).append(" = new QueryColumn(this, \"*\");\n");
        StringJoiner defaultColumnJoiner = new StringJoiner(", ");
        columnInfos.forEach(columnInfo -> {
            if (defaultColumns.contains(columnInfo.getColumn())) {
                String columnPropertyName = StrUtil.buildFieldName(columnInfo.getProperty(), tableDefPropertiesNameStyle);
                if (columnPropertyName.equals(finalTableDefPropertyName)) {
                    columnPropertyName = columnPropertyName + "_";
                }
                defaultColumnJoiner.add(columnPropertyName);
            }
        });
        content.append("    public final QueryColumn[] ").append(StrUtil.buildFieldName("defaultColumns", tableDefPropertiesNameStyle)).append(" = new QueryColumn[]{").append(defaultColumnJoiner).append("};\n\n");
        String schema = !StrUtil.isBlank(tableInfo.getSchema())
                ? tableInfo.getSchema()
                : "";
        String tableName = !StrUtil.isBlank(tableInfo.getTableName())
                ? tableInfo.getTableName()
                : StrUtil.firstCharToLowerCase(tableInfo.getEntitySimpleName());
        content.append("    public ").append(tableDefClassName).append("() {\n")
                .append("        super").append("(\"").append(schema).append("\", \"").append(tableName).append("\");\n")
                .append("    }\n}\n");
        return content.toString();
    }

    /**
     * 构建 Tables 文件内容。
     */
    public static String buildTables(StringBuilder importBuilder, StringBuilder fieldBuilder,
                                     String tablesPackage, String tablesClassName) {
        return "package " + tablesPackage + ";\n\n" +
                importBuilder.toString() +
                "public class " + tablesClassName + " {\n\n" +
                "    private " + tablesClassName + "() {\n" +
                "    }\n\n" +
                fieldBuilder.toString() +
                "\n}\n";
    }

    /**
     * 构建 Tables 文件常量属性。
     */
    public static void buildTablesField(StringBuilder importBuilder, StringBuilder fieldBuilder, TableInfo tableInfo,
                                        String tableDefClassSuffix, String tableDefPropertiesNameStyle, String tableDefInstanceSuffix) {
        String tableDefPackage = StrUtil.buildTableDefPackage(tableInfo.getEntityName());
        String tableDefClassName = tableInfo.getEntitySimpleName().concat(tableDefClassSuffix);
        importBuilder.append("import ").append(tableDefPackage).append('.').append(tableDefClassName).append(";\n");
        fieldBuilder.append("    public static final ").append(tableDefClassName).append(' ')
                .append(StrUtil.buildFieldName(tableInfo.getEntitySimpleName().concat(tableDefInstanceSuffix != null ? tableDefInstanceSuffix.trim() : ""), tableDefPropertiesNameStyle))
                .append(" = new ").append(tableDefClassName).append("();\n");
    }
}
