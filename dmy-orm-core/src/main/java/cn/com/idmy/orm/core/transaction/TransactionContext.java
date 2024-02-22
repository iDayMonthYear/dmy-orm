package cn.com.idmy.orm.core.transaction;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.ibatis.cursor.Cursor;

import java.io.IOException;

/**
 * @author michael
 * 事务管理器上下文
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionContext {
    private static final ThreadLocal<String> xidHolder = new ThreadLocal<>();
    private static final ThreadLocal<Cursor<?>> cursorHolder = new ThreadLocal<>();

    public static String getXID() {
        return xidHolder.get();
    }

    public static void release() {
        xidHolder.remove();
        closeCursor();
    }

    private static void closeCursor() {
        Cursor<?> cursor = cursorHolder.get();
        if (cursor != null) {
            try {
                cursor.close();
            } catch (IOException ignore) {
            } finally {
                cursorHolder.remove();
            }
        }
    }

    public static void holdXID(String xid) {
        xidHolder.set(xid);
    }

    public static void holdCursor(Cursor<?> cursor) {
        cursorHolder.set(cursor);
    }
}
