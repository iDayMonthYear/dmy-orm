package cn.com.idmy.orm.core.constant;

/**
 * SQL 构建常量池。
 *
 * @author 王帅
 * @since 2023-06-12
 */
public interface SqlConsts {
    // === 常用符号 ===

    String EMPTY = "";
    String BLANK = " ";
    String ASTERISK = "*";
    String REFERENCE = ".";
    String SEMICOLON = ";";
    String DELIMITER = ", ";
    String PLACEHOLDER = "?";
    String PERCENT_SIGN = "%";
    String SINGLE_QUOTE = "'";
    String BRACKET_LEFT = "(";
    String BRACKET_RIGHT = ")";
    String HINT_START = "/*+ ";
    String HINT_END = " */ ";

    // === SQL 关键字 ===

    String AS = " AS ";
    String OR = " OR ";
    String END = " END";
    String AND = " AND ";
    String SET = " SET ";
    String CASE = "CASE";
    String WHEN = " WHEN ";
    String THEN = " THEN ";
    String ELSE = " ELSE ";
    String FROM = " FROM ";
    String WHERE = " WHERE ";
    String SELECT = "SELECT ";
    String VALUES = " VALUES ";
    String DELETE = "DELETE";
    String UPDATE = "UPDATE ";
    String HAVING = " HAVING ";
    String DISTINCT = "DISTINCT ";
    String GROUP_BY = " GROUP BY ";
    String ORDER_BY = " ORDER BY ";
    String INSERT = "INSERT";
    String INTO = " INTO ";
    String WITH = "WITH ";
    String RECURSIVE = "RECURSIVE ";
    String INSERT_INTO = INSERT + INTO;
    String DELETE_FROM = DELETE + FROM;
    String SELECT_ALL_FROM = SELECT + ASTERISK + FROM;


    // === Oracle SQl ===

    String INSERT_ALL = "INSERT ALL ";
    String INSERT_ALL_END = " SELECT 1 FROM DUAL";


    // === Limit Offset ===

    String TO = " TO ";
    String TOP = " TOP ";
    String ROWS = " ROWS ";
    String SKIP = " SKIP ";
    String FIRST = " FIRST ";
    String LIMIT = " LIMIT ";
    String OFFSET = " OFFSET ";
    String START_AT = " START AT ";
    String ROWS_ONLY = " ROWS ONLY";
    String ROWS_FETCH_NEXT = " ROWS FETCH NEXT ";


    // === 联表查询关键字 ===

    String ON = " ON ";
    String JOIN = " JOIN ";
    String UNION = " UNION ";
    String UNION_ALL = " UNION ALL ";
    String LEFT_JOIN = " LEFT JOIN ";
    String FULL_JOIN = " FULL JOIN ";
    String RIGHT_JOIN = " RIGHT JOIN ";
    String INNER_JOIN = " INNER JOIN ";
    String CROSS_JOIN = " CROSS JOIN ";


    // === 逻辑符号 ===

    String GT = " > ";
    String GE = " >= ";
    String LT = " < ";
    String LE = " <= ";
    String LIKE = " LIKE ";
    String NOT_LIKE = " NOT LIKE ";
    String EQUALS = " = ";
    String NOT_EQUALS = " != ";
    String IS_NULL = " IS NULL ";
    String IS_NOT_NULL = " IS NOT NULL ";
    String IN = " IN ";
    String NOT_IN = " NOT IN ";
    String BETWEEN = " BETWEEN ";
    String NOT_BETWEEN = " NOT BETWEEN ";


    // === 排序相关关键字 ===

    String ASC = " ASC";
    String DESC = " DESC";
    String NULLS_FIRST = " NULLS FIRST";
    String NULLS_LAST = " NULLS LAST";


    // === 数学运算符 ===

    String PLUS_SIGN = " + ";
    String MINUS_SIGN = " - ";
    String DIVISION_SIGN = " / ";
    String MULTIPLICATION_SIGN = " * ";

    // === 其他拼接需要的字符串 ===

    String EQUALS_PLACEHOLDER = " = ? ";
    String AND_PLACEHOLDER = BLANK + PLACEHOLDER + AND + PLACEHOLDER + BLANK;

}
