package cn.com.idmy.orm.core.dialect;

import cn.com.idmy.orm.core.constant.SqlConsts;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;

import java.util.Collections;
import java.util.Set;

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
    private boolean caseSensitive = false;

    /**
     * 自动把关键字转换为大写
     */
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

    public KeywordWrap(Set<String> keywords, String prefix, String suffix) {
        this(false, keywords, prefix, suffix);
    }

    public KeywordWrap(boolean caseSensitive, Set<String> keywords, String prefix, String suffix) {
        this.caseSensitive = caseSensitive;
        this.keywords = keywords;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public KeywordWrap(boolean caseSensitive, boolean keywordsToUpperCase, Set<String> keywords, String prefix, String suffix) {
        this.caseSensitive = caseSensitive;
        this.keywordsToUpperCase = keywordsToUpperCase;
        this.keywords = keywords;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String wrap(String keyword) {
        if (StrUtil.isBlank(keyword) || SqlConsts.ASTERISK.equals(keyword.trim())) {
            return keyword;
        }

        if (caseSensitive || keywords.isEmpty()) {
            return prefix + keyword + suffix;
        }

        keyword = keywordsToUpperCase ? keyword.toUpperCase() : keyword;
        return keywords.contains(keyword) ? (prefix + keyword + suffix) : keyword;
    }
}
