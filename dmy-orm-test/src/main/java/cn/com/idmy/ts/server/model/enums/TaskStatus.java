package cn.com.idmy.ts.server.model.enums;

import cn.com.idmy.base.model.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskStatus implements IEnum<Integer> {
    NO_EXECUTOR(-99, "无执行器"),
    UNREQUESTED(-100, "未请求"),
    SKIP(-110, "未完成跳过"),
    REQUEST_ERROR(-130, "请求错误"),
    REQUEST_TIMEOUT(-120, "请求超时"),
    GATEWAY_ERROR(-500, "网关错误"),
    WAIT_RESPONSE(0, "等待响应"),
    RESPONSE_ERROR(-200, "响应错误"),
    RESPONSE_TIMEOUT(-201, "响应超时"),
    SUCCESS(200, "成功"),
    ;
    private final Integer value;
    private final String name;
}
