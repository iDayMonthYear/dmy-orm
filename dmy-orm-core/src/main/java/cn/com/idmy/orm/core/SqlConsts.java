package cn.com.idmy.orm.core;

public interface SqlConsts {
    String EMPTY = "";
    String BLANK = " ";
    String ASTERISK = "*";
    String REFERENCE = ".";
    String SEMICOLON = ";";
    String DELIMITER = ", ";
    String PLACEHOLDER = "?";
    String PERCENT_SIGN = "%";
    String SINGLE_QUOTE = "'";
    String STRESS_MARK = "`";
    String BRACKET_LEFT = "(";
    String BRACKET_RIGHT = ")";
    String HINT_START = "/*+ ";
    String HINT_END = " */ ";

    String AS = " AS ";
    String OR = " OR ";
    String END = " END";
    String AND = " AND ";
    String SET = " SET ";
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

    String LIMIT = " LIMIT ";
    String OFFSET = " OFFSET ";


    String ASC = " ASC";
    String DESC = " DESC";
    String NULLS_FIRST = " NULLS FIRST";
    String NULLS_LAST = " NULLS LAST";

    String EQUALS_PLACEHOLDER = " = ? ";
    String AND_PLACEHOLDER = BLANK + PLACEHOLDER + AND + PLACEHOLDER + BLANK;
}