package cn.com.idmy.orm.core.transaction;


import org.apache.ibatis.cursor.Cursor;

import java.io.IOException;

/**
 * @author michael
 * 事务管理器上下文
 */
public class TransactionContext {

    private TransactionContext() {
    }

    private static final ThreadLocal<String> XID_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<Cursor<?>> CURSOR_HOLDER = new ThreadLocal<>();

    public static String getXID() {
        return XID_HOLDER.get();
    }

    public static void release() {
        XID_HOLDER.remove();
        closeCursor();
    }

    private static void closeCursor() {
        Cursor<?> cursor = CURSOR_HOLDER.get();
        if (cursor != null) {
            try {
                cursor.close();
            } catch (IOException e) {
                //ignore
            } finally {
                CURSOR_HOLDER.remove();
            }
        }
    }

    public static void holdXID(String xid) {
        XID_HOLDER.set(xid);
    }

    public static void holdCursor(Cursor<?> cursor) {
        CURSOR_HOLDER.set(cursor);
    }

}
