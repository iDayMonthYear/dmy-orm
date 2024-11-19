package cn.com.idmy.ts.server.model.entity;
import cn.com.idmy.orm.annotation.Table;
import cn.com.idmy.orm.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table
public class App  {
    @TableId
    protected String key;
    protected String name;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
}
