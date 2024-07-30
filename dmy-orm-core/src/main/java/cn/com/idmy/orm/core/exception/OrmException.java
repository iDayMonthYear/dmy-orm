package cn.com.idmy.orm.core.exception;

import cn.com.idmy.orm.core.exception.locale.Localizable;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * @author michael
 * @author 王帅
 */
public class OrmException extends RuntimeException {
    private Localizable pattern;
    private Object[] arguments;

    public OrmException(Throwable cause, Localizable pattern, Object[] arguments) {
        super(cause);
        this.pattern = pattern;
        this.arguments = arguments;
    }

    public OrmException(Localizable pattern, Object... arguments) {
        this.pattern = pattern;
        this.arguments = arguments;
    }

    public OrmException(String message) {
        super(message);
    }

    public OrmException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrmException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return getMessage(Locale.CHINESE);
    }

    @Override
    public String getLocalizedMessage() {
        return getMessage(Locale.getDefault());
    }

    private String getMessage(Locale locale) {
        if (pattern == null) {
            return super.getMessage();
        }
        String localizedString = pattern.getLocalizedString(locale);
        return MessageFormat.format(localizedString, arguments);
    }

}
