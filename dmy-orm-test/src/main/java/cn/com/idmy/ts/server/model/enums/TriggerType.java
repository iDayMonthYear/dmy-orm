package cn.com.idmy.ts.server.model.enums;

import cn.com.idmy.base.model.IEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TriggerType implements IEnum<Integer> {
    MANUAL(1, "手动触发"),
    CRON(2, "Cron触发"),
    RETRY(3, "重试触发"),
    PARENT(4, "父任务触发"),
    API(5, "接口触发"),
    MISFIRE(6, "过期补偿");

    private final Integer value;
    private final String name;
}
