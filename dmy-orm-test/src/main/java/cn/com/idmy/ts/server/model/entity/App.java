package cn.com.idmy.ts.server.model.entity;

import cn.com.idmy.base.annotation.Id;
import cn.com.idmy.base.annotation.IdType;
import cn.com.idmy.base.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(of = "id", callSuper = false)
@Data
@Accessors(fluent = false)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(title = "应用", name = "Dmy", idType = IdType.AUTO)
public class App extends BaseApp {
    @Id(type = IdType.AUTO)
    protected Long id;
    protected String name;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
}
