package cn.com.idmy.orm.listener;

import cn.com.idmy.orm.annotation.WatchEnum.Action;
import cn.com.idmy.orm.annotation.WatchEnum.Timing;

public record EnumWatchEvent(Class<?> entityClass, Class<?> enumClass, Action action, Timing timing, Object entity) {
}