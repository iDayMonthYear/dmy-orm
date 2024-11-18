package cn.com.idmy.orm.core.listener;

import cn.com.idmy.orm.annotation.WatchEnum.WatchAction;
import cn.com.idmy.orm.annotation.WatchEnum.WatchTiming;

public record EnumWatchEvent(Class<?> entityClass, Class<?> enumClass, WatchAction action, WatchTiming timing,
                             Object entity) {
}