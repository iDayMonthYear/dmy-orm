package cn.com.idmy.ts.server.service.impl;


import cn.com.idmy.orm.core.ast.SelectChain;
import cn.com.idmy.ts.server.dao.AppDao;
import cn.com.idmy.ts.server.model.entity.App;
import cn.com.idmy.ts.server.service.AppService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AppServiceImpl  implements AppService {
    private final AppDao appDao;

    @Override
    public List<App> all() {
        SelectChain<App> chain = SelectChain.of(appDao);
        chain.in(App::getKey, "dmy-ts-admin", "saas-invoice-admin");
        List<App> apps = appDao.find(chain);
        return apps;
    }
}
