package cn.com.idmy.orm.core.listener;

/**
 * 监听器接口。
 *
 * @author snow
 * @since 2023/4/28
 */
public interface Listener extends Comparable<Listener> {

    /**
     * <p>多个监听器时的执行顺序。
     *
     * <p>值越小越早触发执行。
     *
     * @return order
     */
    default int order() {
        return Integer.MAX_VALUE;
    }

    @Override
    default int compareTo(Listener other) {
        return order() - other.order();
    }
}
