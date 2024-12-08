package cn.com.idmy.ts.server.model.entity;

import cn.com.idmy.base.annotation.Table;
import cn.com.idmy.base.annotation.Table.Column;
import cn.com.idmy.base.annotation.Table.Id;
import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Accessors(fluent = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table("app")
public class App  {
    @Id
    protected Long id;
    @Column("t_key")
    protected String key;
    protected String name;
    protected JSONObject json;
    protected Map<Integer, Long> json2;
    protected Long a;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
}
