package cn.com.idmy.orm.test;

import cn.com.idmy.orm.annotation.Table;
import cn.com.idmy.orm.annotation.TableField;
import cn.com.idmy.orm.core.ast.DeleteChain;
import cn.com.idmy.orm.core.ast.SelectChain;
import cn.com.idmy.orm.core.ast.SqlFn;
import cn.com.idmy.orm.core.ast.UpdateChain;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.hutool.core.lang.Console;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(fluent = true)
@Table("t_user")
public class User {
    private Long id;
    private String name;
    private String username;
    private String mobile;
    private String email;
    @TableField("created_at")
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static void main(String[] args) {
        UserDao dao = new UserDao() {
            @Override
            public List<User> find(SelectChain<User> chain) {
                return List.of();
            }

            @Override
            public User get(SelectChain<User> chain) {
                return null;
            }

            @Override
            public int update(UpdateChain<User> chain) {
                return 0;
            }

            @Override
            public int delete(DeleteChain<User> chain) {
                return 0;
            }
        };

        SelectChain<User> chain = SelectChain.of(dao)
                .eq(User::id, c -> c.plus(1))  // 使用函数构建表达式
                .eq(User::id, c -> c.divide(100))
                .eq(User::id, 1)  // 普通字符串值
                .or()
                .in(User::createdAt, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now())
                .select(() -> SqlFn.ifNull(User::id, 888))
                .select(User::id, User::id, User::name, User::createdAt)
                .distinct(User::id)
                .groupBy(User::id, User::name)
                .orderBy(User::id, true, User::name, false)
                .orderBy(new String[]{"test", "desc"})
                .eq(User::username, "test");// 普通字符串值
        Console.log(chain.sql());
    }
}
