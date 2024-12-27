package cn.com.idmy.ts.server.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@Accessors(fluent = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseApp {
    private Long creatorId;
    private String creator;
}
