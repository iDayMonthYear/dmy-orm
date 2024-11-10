package cn.com.idmy.orm.test;

import cn.com.idmy.orm.annotation.Table;
import cn.com.idmy.orm.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

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
}
