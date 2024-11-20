package cn.com.idmy.ts.server.model.enums;

import cn.com.idmy.base.model.IEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NativeTask implements IEnum<Integer> {
    UPDATE_TIMEOUT_TASK_STATUS(-1, "更新超时任务状态"),
    DEREGISTER_EXECUTOR(-2, "撤销执行器"),
    DELETE_DEAD_LOG(-3, "删除日志"),
    HOLIDAY_DATA_SYNC(-4, "节假日数据同步");

    private final Integer value;
    private final String name;

    public static boolean isNative(int id) {
        return id < 0;
    }
}
