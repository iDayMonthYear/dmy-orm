package cn.com.idmy.orm.core.mask;

/**
 * 数据脱敏处理器
 */
public interface MaskProcessor {

    Object mask(Object data);

}
