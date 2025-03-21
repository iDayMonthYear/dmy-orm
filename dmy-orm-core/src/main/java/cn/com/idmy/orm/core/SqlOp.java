package cn.com.idmy.orm.core;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true, chain = false)
public class SqlOp<V extends Number> {
    private @NotNull String op;
    private @NotNull V value;

    public @NotNull SqlOp<V> plus(@NonNull V val) {
        return new SqlOp<>("+", val);
    }

    public @NotNull SqlOp<V> minus(@NonNull V val) {
        return new SqlOp<>("-", val);
    }

    public @NotNull SqlOp<V> multiply(@NonNull V val) {
        return new SqlOp<>("*", val);
    }

    public @NotNull SqlOp<V> divide(@NonNull V val) {
        return new SqlOp<>("/", val);
    }

    public @NotNull SqlOp<V> mod(@NonNull V val) {
        return new SqlOp<>("%", val);
    }
}
