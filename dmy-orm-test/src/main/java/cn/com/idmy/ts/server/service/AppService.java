package cn.com.idmy.ts.server.service;

import cn.com.idmy.base.model.Page;
import cn.com.idmy.ts.server.model.entity.App;

import java.util.Collection;
import java.util.List;

public interface AppService  {
    List<App> all();

    App get(Long[] id);

    List<App> find(Collection<Long[]> ids);
    
    Page<App> testCrud();
    
    void testBatchOperations();
    
    void testQueryConditions();
}
