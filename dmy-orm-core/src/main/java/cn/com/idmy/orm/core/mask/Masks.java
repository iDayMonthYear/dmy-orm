package cn.com.idmy.orm.core.mask;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 内置的数据脱敏方式
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Masks {
    public static final String MOBILE = "mobile";
    public static final String ID_CARD_NO = "idCardNo";
    public static final String PASSWORD = "password";

    private static String createMask(int count) {
        return "*".repeat(Math.max(0, count));
    }

    private static String mask(String needToMaskString, int keepFirstCount, int keepLastCount, int maskCount) {
        return needToMaskString.substring(0, keepFirstCount)
                + createMask(maskCount)
                + needToMaskString.substring(needToMaskString.length() - keepLastCount);
    }

    /**
     * 手机号脱敏处理器
     * 保留前三后四，中间的为星号  "*"
     */
    static MaskProcessor MOBILE_PROCESSOR = data -> {
        if (data instanceof String && ((String) data).startsWith("1") && ((String) data).length() == 11) {
            return mask((String) data, 3, 4, 4);
        } else {
            return data;
        }
    };


    /**
     * 身份证号脱敏处理器
     * 身份证号的保留前三后四，中间的数为星号  "*"
     */
    static MaskProcessor ID_CARD_NO_PROCESSOR = data -> {
        if (data instanceof String && ((String) data).length() >= 15) {
            return mask((String) data, 3, 4, ((String) data).length() - 7);
        }
        return data;
    };


    /**
     * 密码 脱敏
     */
    static MaskProcessor PASSWORD_PROCESSOR = data -> {
        if (data instanceof String) {
            return mask((String) data, 0, 0, ((String) data).length());
        }
        return data;
    };
}
