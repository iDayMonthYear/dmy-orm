package cn.com.idmy.orm.core.logicdelete;

import cn.com.idmy.orm.core.logicdelete.impl.DefaultLogicDeleteProcessor;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.function.Supplier;

/**
 * 逻辑删除管理器。
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogicDeleteManager {
    private static final ThreadLocal<Boolean> ignore = new ThreadLocal<>();
    @Getter
    @Setter
    private static LogicDeleteProcessor processor = new DefaultLogicDeleteProcessor();

    /**
     * 跳过逻辑删除字段处理，直接进行数据库物理操作。
     */
    public static <T> T ignore(Supplier<T> supplier) {
        try {
            start();
            return supplier.get();
        } finally {
            restore();
        }
    }

    /**
     * 跳过逻辑删除字段处理，直接进行数据库物理操作。
     */
    public static void ignore(Runnable runnable) {
        try {
            start();
            runnable.run();
        } finally {
            restore();
        }
    }

    public static void start() {
        ignore.set(Boolean.TRUE);
    }

    public static void restore() {
        ignore.remove();
    }

    /**
     * 获取逻辑删除列，返回 {@code null} 表示跳过逻辑删除。
     *
     * @param logicDeleteColumn 逻辑删除列
     * @return 逻辑删除列
     */
    @Nullable
    public static String getLogicDeleteColumn(String logicDeleteColumn) {
        if (logicDeleteColumn == null) {
            return null;
        }
        Boolean bol = ignore.get();
        if (bol == null) {
            return logicDeleteColumn;
        } else {
            return bol ? null : logicDeleteColumn;
        }
    }
}
