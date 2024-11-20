package cn.com.idmy.ts.server.model.entity;

import cn.com.idmy.orm.annotation.Table;
import cn.com.idmy.ts.server.model.enums.TaskStatus;
import cn.com.idmy.ts.server.model.enums.TriggerType;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Accessors(chain = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Table("ts_taskLog")
public class TaskLog {
    protected Long id;
    protected Integer taskId;
    @Nullable
    protected String address;
    protected TaskStatus status;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
    protected LocalDateTime deathAt;
    @Nullable
    protected LocalDateTime callbackAt;
    protected Integer retry;
    protected LocalDateTime timeout;
    @Nullable
    protected String content;
    protected TriggerType triggerType;

    public void setContent(String content) {
        if (content == null) {
            this.content = null;
        } else if (content.length() > 15000) {
            this.content = content.substring(15000);
        } else {
            this.content = content;
        }
    }
}
