package cn.com.idmy.ts.server.config;

import cn.com.idmy.orm.core.CrudInterceptor;
import cn.com.idmy.ts.server.model.entity.App;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

// 关心插入和更新的拦截器
public class AuditInterceptor implements CrudInterceptor {
    @Override
    public void beforeInsert(Object entity) {
        if (entity instanceof App app) {
            app.createdAt(LocalDateTime.now());
            app.updatedAt(LocalDateTime.now());
        }
    }

    @Override
    public void beforeUpdate(Object entity) {
        if (entity instanceof App app) {
            app.updatedAt(LocalDateTime.now());
        }
    }

    @Override
    public Set<CrudType> getInterceptTypes() {
        return EnumSet.of(CrudType.INSERT, CrudType.UPDATE);
    }
}