package cn.com.idmy.orm.core.logicdelete;

import cn.com.idmy.orm.core.logicdelete.impl.DefaultLogicDeleteProcessor;
import jakarta.annotation.Nullable;
import lombok.Getter;

import java.util.function.Supplier;

/**
 * 逻辑删除管理器。
 */
public class LogicDeleteManager {

    private LogicDeleteManager() {
    }

    /**
     * -- GETTER --
     * 获取逻辑删除处理器。
     *
     * @return 逻辑删除处理器
     */
    @Getter
    private static LogicDeleteProcessor processor = new DefaultLogicDeleteProcessor();
    private static final ThreadLocal<Boolean> skipFlags = new ThreadLocal<>();

    /**
     * 设置逻辑删除处理器。
     *
     * @param processor 逻辑删除处理器
     */
    public static void setProcessor(LogicDeleteProcessor processor) {
        LogicDeleteManager.processor = processor;
    }

    /**
     * 跳过逻辑删除字段处理，直接进行数据库物理操作。
     */
    public static <T> T execWithoutLogicDelete(Supplier<T> supplier) {
        try {
            skipLogicDelete();
            return supplier.get();
        } finally {
            restoreLogicDelete();
        }
    }

    /**
     * 跳过逻辑删除字段处理，直接进行数据库物理操作。
     */
    public static void execWithoutLogicDelete(Runnable runnable) {
        try {
            skipLogicDelete();
            runnable.run();
        } finally {
            restoreLogicDelete();
        }
    }

    /**
     * 跳过逻辑删除字段处理。
     */
    public static void skipLogicDelete() {
        skipFlags.set(Boolean.TRUE);
    }

    /**
     * 恢复逻辑删除字段处理。
     */
    public static void restoreLogicDelete() {
        skipFlags.remove();
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
        Boolean skipFlag = skipFlags.get();
        if (skipFlag == null) {
            return logicDeleteColumn;
        }
        return skipFlag ? null : logicDeleteColumn;
    }

}
