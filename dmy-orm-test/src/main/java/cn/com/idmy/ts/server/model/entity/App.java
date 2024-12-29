package cn.com.idmy.ts.server.model.entity;

import cn.com.idmy.base.annotation.Table;
import cn.com.idmy.base.annotation.Table.Column;
import cn.com.idmy.base.annotation.Table.Id;
import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Map;

@EqualsAndHashCode(of = "id", callSuper = false)
@Data
@Accessors(fluent = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(value = "应用", name = "app")
public class App extends BaseApp {
    @Id
    protected Long id;
    @Column(name = "t_key", value = "Key")
    protected String key;
    @Column("名称")
    protected String name;
    @Column("JSON")
    protected JSONObject json;
    protected Map<Integer, Long> json2;
    protected Long a;
    @Column("创建时间")
    protected LocalDateTime createdAt;
    @Column("更新时间")
    protected LocalDateTime updatedAt;
}
