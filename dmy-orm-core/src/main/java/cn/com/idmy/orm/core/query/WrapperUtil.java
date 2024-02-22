package cn.com.idmy.orm.core.query;


import cn.com.idmy.orm.core.OrmConsts;
import cn.com.idmy.orm.core.constant.SqlConsts;
import cn.com.idmy.orm.core.dialect.Dialect;
import cn.com.idmy.orm.core.dialect.DialectFactory;
import cn.com.idmy.orm.core.util.ClassUtil;
import cn.com.idmy.orm.core.util.EnumWrapper;
import cn.hutool.core.util.StrUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class WrapperUtil {
    private WrapperUtil() {
    }

    static List<QueryWrapper> getChildQueryWrapper(QueryCondition condition) {
        List<QueryWrapper> list = null;
        while (condition != null) {
            if (condition.checkEffective()) {
                if (condition instanceof Brackets) {
                    List<QueryWrapper> childQueryWrapper = getChildQueryWrapper(((Brackets) condition).getChildCondition());
                    if (!childQueryWrapper.isEmpty()) {
                        if (list == null) {
                            list = new ArrayList<>();
                        }
                        list.addAll(childQueryWrapper);
                    }
                }
                // not Brackets
                else {
                    Object value = condition.getValue();
                    if (value instanceof QueryWrapper) {
                        if (list == null) {
                            list = new ArrayList<>();
                        }
                        list.add((QueryWrapper) value);
                        list.addAll(((QueryWrapper) value).getChildSelect());
                    } else if (value != null && value.getClass().isArray()) {
                        for (int i = 0; i < Array.getLength(value); i++) {
                            Object arrayValue = Array.get(value, i);
                            if (arrayValue instanceof QueryWrapper) {
                                if (list == null) {
                                    list = new ArrayList<>();
                                }
                                list.add((QueryWrapper) arrayValue);
                                list.addAll(((QueryWrapper) arrayValue).getChildSelect());
                            }
                        }
                    }
                }
            }
            condition = condition.next;
        }
        return list == null ? Collections.emptyList() : list;
    }


    static Object[] getValues(QueryCondition condition) {
        if (condition == null) {
            return OrmConsts.EMPTY_ARRAY;
        }

        List<Object> params = new ArrayList<>();
        getValues(condition, params);

        return params.isEmpty() ? OrmConsts.EMPTY_ARRAY : params.toArray();
    }


    private static void getValues(QueryCondition condition, List<Object> params) {
        if (condition == null) {
            return;
        }

        Object value = condition.getValue();
        if (value == null
                || value instanceof QueryColumn
                || value instanceof RawQueryCondition) {
            getValues(condition.next, params);
            return;
        }

        addParam(params, value);
        getValues(condition.next, params);
    }

    private static void addParam(List<Object> paras, Object value) {
        if (value == null) {
            paras.add(null);
        } else if (ClassUtil.isArray(value.getClass())) {
            for (int i = 0; i < Array.getLength(value); i++) {
                addParam(paras, Array.get(value, i));
            }
        } else if (value instanceof QueryWrapper) {
            Object[] valueArray = ((QueryWrapper) value).getAllValueArray();
            paras.addAll(Arrays.asList(valueArray));
        } else if (value.getClass().isEnum()) {
            EnumWrapper enumWrapper = EnumWrapper.of(value.getClass());
            if (enumWrapper.hasEnumValueAnnotation()) {
                paras.add(enumWrapper.getEnumValue((Enum) value));
            } else {
                paras.add(((Enum<?>) value).name());
            }
        } else {
            paras.add(value);
        }

    }

    static String buildValue(List<QueryTable> queryTables, Object value) {
        if (value instanceof Number || value instanceof Boolean) {
            return String.valueOf(value);
        } else if (value instanceof RawQueryCondition) {
            return ((RawQueryCondition) value).getContent();
        } else if (value instanceof QueryColumn) {
            return ((QueryColumn) value).toConditionSql(queryTables, DialectFactory.getDialect());
        } else {
            return SqlConsts.SINGLE_QUOTE + value + SqlConsts.SINGLE_QUOTE;
        }
    }


    static String withBracket(String sql) {
        return SqlConsts.BRACKET_LEFT + sql + SqlConsts.BRACKET_RIGHT;
    }

    static String withAlias(String sql, String alias, Dialect dialect) {
        return SqlConsts.BRACKET_LEFT + sql + SqlConsts.BRACKET_RIGHT + buildColumnAlias(alias, dialect);
    }

    static String buildAlias(String alias, Dialect dialect) {
        return StrUtil.isBlank(alias) ? SqlConsts.EMPTY : getAsKeyWord(dialect) + dialect.wrap(alias);
    }

    static String buildColumnAlias(String alias, Dialect dialect) {
        return StrUtil.isBlank(alias) ? SqlConsts.EMPTY : getAsKeyWord(dialect) + dialect.wrapColumnAlias(alias);
    }

    private static String getAsKeyWord(Dialect dialect) {
        return SqlConsts.AS;
    }
}
