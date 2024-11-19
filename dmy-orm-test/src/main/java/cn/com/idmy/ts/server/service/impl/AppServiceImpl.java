package cn.com.idmy.ts.server.service.impl;


import cn.com.idmy.orm.core.SelectChain;
import cn.com.idmy.ts.server.dao.AppDao;
import cn.com.idmy.ts.server.model.entity.App;
import cn.com.idmy.ts.server.service.AppService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AppServiceImpl implements AppService {
    private final AppDao appDao;

    @Override
    public List<App> all() {
        SelectChain<App> chain = SelectChain.of(appDao);
        chain.in(App::getKey, "dmy-ts-admin", "saas-invoice-admin");
        return appDao.find(chain);
    }

    @Override
    public App get(String id) {
        return appDao.get(id);
    }

    @Override
    public List<App> find(Collection<String> ids) {
        return appDao.find(ids);
    }
}
