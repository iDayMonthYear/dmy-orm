package cn.com.idmy.orm.core;

public interface SqlConsts {
    String EMPTY = "";
    String BLANK = " ";
    String ASTERISK = "*";
    String DELIMITER = ", ";
    String PLACEHOLDER = "?";
    String STRESS_MARK = "`";
    String BRACKET_LEFT = "(";
    String BRACKET_RIGHT = ")";

    String OR = " or ";
    String AND = " and ";
    String SET = " set ";
    String FROM = " from ";
    String WHERE = " where ";
    String SELECT = "select ";
    String VALUES = " values ";
    String DELETE = "delete";
    String UPDATE = "update ";
    String DISTINCT = "distinct ";
    String GROUP_BY = " group by ";
    String ORDER_BY = " order by ";
    String INSERT = "insert";
    String INTO = " into ";
    String INSERT_INTO = INSERT + INTO;
    String DELETE_FROM = DELETE + FROM;

    String LIMIT = " limit ";
    String OFFSET = " offset ";

    String DESC = " desc";

    String EQUAL = " = ";
    String EQUALS_PLACEHOLDER = " = ? ";
}