package cn.com.idmy.orm.mybatis;

import org.jetbrains.annotations.NotNull;

public interface IdGenerator {
    @NotNull
    Object generate(@NotNull Object entity, @NotNull String column);
}
