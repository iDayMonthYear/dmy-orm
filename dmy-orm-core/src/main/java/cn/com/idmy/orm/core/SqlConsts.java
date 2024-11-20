package cn.com.idmy.orm.core;

public final class SqlConsts {

    private SqlConsts() {
    }


    public static final String EMPTY = "";
    public static final String BLANK = " ";
    public static final String ASTERISK = "*";
    public static final String REFERENCE = ".";
    public static final String SEMICOLON = ";";
    public static final String DELIMITER = ", ";
    public static final String PLACEHOLDER = "?";
    public static final String PERCENT_SIGN = "%";
    public static final String SINGLE_QUOTE = "'";
    public static final String STRESS_MARK = "`";
    public static final String BRACKET_LEFT = "(";
    public static final String BRACKET_RIGHT = ")";
    public static final String HINT_START = "/*+ ";
    public static final String HINT_END = " */ ";

    public static final String AS = " AS ";
    public static final String OR = " OR ";
    public static final String END = " END";
    public static final String AND = " AND ";
    public static final String SET = " SET ";
    public static final String FROM = " FROM ";
    public static final String WHERE = " WHERE ";
    public static final String SELECT = "SELECT ";
    public static final String VALUES = " VALUES ";
    public static final String DELETE = "DELETE";
    public static final String UPDATE = "UPDATE ";
    public static final String HAVING = " HAVING ";
    public static final String DISTINCT = "DISTINCT ";
    public static final String GROUP_BY = " GROUP BY ";
    public static final String ORDER_BY = " ORDER BY ";
    public static final String INSERT = "INSERT";
    public static final String INTO = " INTO ";
    public static final String WITH = "WITH ";
    public static final String RECURSIVE = "RECURSIVE ";
    public static final String INSERT_INTO = INSERT + INTO;
    public static final String DELETE_FROM = DELETE + FROM;
    public static final String SELECT_ALL_FROM = SELECT + ASTERISK + FROM;


    public static final String LIMIT = " LIMIT ";
    public static final String OFFSET = " OFFSET ";


    public static final String ASC = " ASC";
    public static final String DESC = " DESC";
    public static final String NULLS_FIRST = " NULLS FIRST";
    public static final String NULLS_LAST = " NULLS LAST";


    public static final String EQUALS_PLACEHOLDER = " = ? ";
    public static final String AND_PLACEHOLDER = BLANK + PLACEHOLDER + AND + PLACEHOLDER + BLANK;
}