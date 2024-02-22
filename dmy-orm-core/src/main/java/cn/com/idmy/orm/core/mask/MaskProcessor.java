package cn.com.idmy.orm.core.mask;

@FunctionalInterface
public interface MaskProcessor {
    Object mask(Object data);
}
