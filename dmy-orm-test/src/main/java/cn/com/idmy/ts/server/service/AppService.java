package cn.com.idmy.ts.server.service;

import cn.com.idmy.ts.server.model.entity.App;

import java.util.Collection;
import java.util.List;

public interface AppService  {
    List<App> all();

    App get(Long id);

    List<App> find(Collection<Long> ids);
    
    void testCrud();
    
    void testBatchOperations();
    
    void testQueryConditions();
}
