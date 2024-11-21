package cn.com.idmy.ts.server.model.entity;

import cn.com.idmy.orm.annotation.Table;
import cn.com.idmy.orm.annotation.Table.Id;
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
    @Id
    protected Long id;
    protected String key;
    protected String name;
    protected Long a;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
}
