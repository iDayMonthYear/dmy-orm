package cn.com.idmy.orm.core.exception;

import cn.com.idmy.orm.core.exception.locale.Localizable;

/**
 * MybatisFlexException 异常封装类
 */
public final class OrmExceptions {

    private OrmExceptions() {
    }


    /**
     * 封装 MybatisFlexException 异常
     *
     * @param throwable 异常
     * @return MybatisFlexException
     */
    public static OrmException wrap(Throwable throwable) {
        if (throwable instanceof OrmException) {
            return (OrmException) throwable;
        }
        return new OrmException(throwable);
    }


    /**
     * 封装 MybatisFlexException 异常
     *
     * @param throwable 异常
     * @param msg       消息
     * @param params    消息参数
     * @return MybatisFlexException
     */
    public static OrmException wrap(Throwable throwable, String msg, Object... params) {
        return new OrmException(String.format(msg, params), throwable);
    }

    /**
     * 封装 MybatisFlexException 异常
     *
     * @param msg    消息
     * @param params 消息参数
     * @return MybatisFlexException
     */
    public static OrmException wrap(String msg, Object... params) {
        return new OrmException(String.format(msg, params));
    }

    public static OrmException wrap(Throwable cause, Localizable pattern, Object... args) {
        return new OrmException(cause, pattern, args);
    }

    public static OrmException wrap(Localizable pattern, Object... args) {
        return new OrmException(pattern, args);
    }

}
