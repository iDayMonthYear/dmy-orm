package cn.com.idmy.orm.core.dialect;

import cn.com.idmy.orm.core.constant.SqlConsts;
import cn.com.idmy.orm.core.util.StringUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用于对数据库的关键字包装
 */
@Getter
public class KeywordWrap {

    /**
     * 无反义处理, 适用于 db2, informix, clickhouse 等
     */
    public static final KeywordWrap NONE = new KeywordWrap("", "") {
        @Override
        public String wrap(String keyword) {
            return keyword;
        }
    };

    /**
     * 无反义区分大小写处理, 适用于 db2, informix, clickhouse 等
     */
    public static final KeywordWrap NONE_CASE_SENSITIVE = new KeywordWrap(true, "", "") {
        @Override
        public String wrap(String keyword) {
            return keyword;
        }
    };

    /**
     * 反引号反义处理, 适用于 mysql, h2 等
     */
    public static final KeywordWrap BACK_QUOTE = new KeywordWrap("`", "`");

    /**
     * 双引号反义处理, 适用于 postgresql, sqlite, derby, oracle 等
     */
    public static final KeywordWrap DOUBLE_QUOTATION = new KeywordWrap("\"", "\"");

    /**
     * 方括号反义处理, 适用于 sqlserver
     */
    public static final KeywordWrap SQUARE_BRACKETS = new KeywordWrap("[", "]");
    /**
     * 大小写敏感
     */
    @Setter
    private boolean caseSensitive = false;

    /**
     * 自动把关键字转换为大写
     */
    @Setter
    private boolean keywordsToUpperCase = false;
    /**
     * 数据库关键字
     */
    private final Set<String> keywords;
    /**
     * 前缀
     */
    private final String prefix;
    /**
     * 后缀
     */
    private final String suffix;


    public KeywordWrap(String prefix, String suffix) {
        this(false, Collections.emptySet(), prefix, suffix);
    }


    public KeywordWrap(boolean caseSensitive, String prefix, String suffix) {
        this(caseSensitive, Collections.emptySet(), prefix, suffix);
    }

    public KeywordWrap(Set<String> keywords, String prefix, String suffix) {
        this(false, keywords, prefix, suffix);
    }

    public KeywordWrap(boolean caseSensitive, Set<String> keywords, String prefix, String suffix) {
        this.caseSensitive = caseSensitive;
        this.keywords = keywords.stream().map(String::toUpperCase).collect(Collectors.toSet());
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public KeywordWrap(boolean caseSensitive, boolean keywordsToUpperCase, Set<String> keywords, String prefix,
                       String suffix) {
        this.caseSensitive = caseSensitive;
        this.keywordsToUpperCase = keywordsToUpperCase;
        this.keywords = keywords.stream().map(String::toUpperCase).collect(Collectors.toSet());
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String wrap(String keyword) {
        if (StringUtil.isBlank(keyword) || SqlConsts.ASTERISK.equals(keyword.trim())) {
            return keyword;
        }

        if (caseSensitive || keywords.isEmpty()) {
            return prefix + keyword + suffix;
        }

        if (keywordsToUpperCase) {
            keyword = keyword.toUpperCase();
            return keywords.contains(keyword) ? (prefix + keyword + suffix) : keyword;
        } else {
            return keywords.contains(keyword.toUpperCase()) ? (prefix + keyword + suffix) : keyword;
        }
    }

    //数据scheme table 包装 根据 . 分割后分别包装
    public String wrapKeyword(String keyword) {
        StringBuilder resultBuilder = new StringBuilder();
        String[] split = keyword.split("\\.");
        if (split.length > 0) {
            Arrays.asList(split)
                    .forEach(f -> resultBuilder.append(prefix).append(f).append(suffix).append("."));
            return resultBuilder.toString().substring(0, resultBuilder.length() - 1);
        } else {
            return prefix + keyword + suffix;
        }
    }
}
