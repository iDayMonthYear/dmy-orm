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

    String AS = " as ";
    String OR = " or ";
    String END = " END";
    String AND = " and ";
    String SET = " set ";
    String FROM = " from ";
    String WHERE = " where ";
    String SELECT = "select ";
    String VALUES = " values ";
    String DELETE = "delete";
    String UPDATE = "update ";
    String HAVING = " having ";
    String DISTINCT = "distinct ";
    String GROUP_BY = " group by ";
    String ORDER_BY = " order by ";
    String INSERT = "insert";
    String INTO = " into ";
    String WITH = "with ";
    String RECURSIVE = "recursive ";
    String INSERT_INTO = INSERT + INTO;
    String DELETE_FROM = DELETE + FROM;
    String SELECT_ALL_FROM = SELECT + ASTERISK + FROM;

    String LIMIT = " limit ";
    String OFFSET = " offset ";


    String ASC = " asc";
    String DESC = " desc";
    String NULLS_FIRST = " nulls first";
    String NULLS_LAST = " nulls last";

    String EQUALS_PLACEHOLDER = " = ? ";
    String AND_PLACEHOLDER = BLANK + PLACEHOLDER + AND + PLACEHOLDER + BLANK;
}