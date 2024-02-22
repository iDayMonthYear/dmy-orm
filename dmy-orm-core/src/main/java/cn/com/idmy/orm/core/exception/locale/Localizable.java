package cn.com.idmy.orm.core.exception.locale;

import java.io.Serializable;
import java.util.Locale;

/**
 * 可本地化字符串接口。
 *
 * @author 王帅
 * @since 2023-07-26
 */
public interface Localizable extends Serializable {

    /**
     * 获取源（非本地化）字符串。
     *
     * @return 源字符串
     */
    String getSourceString();

    /**
     * 获取本地化字符串。
     *
     * @param locale 要获取字符串的区域
     * @return 本地化字符串或源字符串（如果没有可用的本地化版本）
     */
    String getLocalizedString(Locale locale);

}
