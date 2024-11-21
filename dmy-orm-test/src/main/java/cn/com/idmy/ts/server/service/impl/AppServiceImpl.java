package cn.com.idmy.ts.server.service.impl;

import cn.com.idmy.orm.core.SelectChain;
import cn.com.idmy.ts.server.dao.AppDao;
import cn.com.idmy.ts.server.model.entity.App;
import cn.com.idmy.ts.server.service.AppService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    public App get(Long id) {
        return appDao.get(id);
    }

    @Override
    public List<App> find(Collection<Long> ids) {
        return appDao.find(ids);
    }

    @Override
    public void testCrud() {
        System.out.println("=== Testing CRUD operations ===");

        // Test create
        App newApp1 = App.builder()
                .key("test-app")
                .name("Test App")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        App newApp2 = App.builder()
                .key("test-app")
                .name("Test App")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        appDao.creates(List.of(newApp1, newApp2));

        // Test read
        App app = get(newApp1.getId());
        System.out.println("Read app: " + app);

        // Test read all
        List<App> allApps = all();
        System.out.println("Total apps: " + allApps.size());

        System.out.println("CRUD test completed");
    }

    @Override
    public void testBatchOperations() {
        System.out.println("=== Testing batch operations ===");

        // Test batch query
        List<App> apps = find(List.of(1L, 2L));
        System.out.println("Found " + apps.size() + " apps in batch query");

        // Test using SelectChain
        SelectChain<App> chain = SelectChain.of(appDao);
        chain.in(App::getId, 1, 2);
        List<App> chainResult = appDao.find(chain);
        System.out.println("Found " + chainResult.size() + " apps using SelectChain");

        System.out.println("Batch operations test completed");
    }

    @Override
    public void testQueryConditions() {
        System.out.println("=== Testing query conditions ===");

        // Test different query conditions
        SelectChain<App> chain = SelectChain.of(appDao);

        // Test like condition
        List<App> adminApps = appDao.find(chain);
        System.out.println("Found " + adminApps.size() + " admin apps");

        // Test multiple conditions
        chain = SelectChain.of(appDao);
        chain.in(App::getKey, "dmy-ts-admin")
                .gt(App::getCreatedAt, LocalDateTime.now().minusDays(30));
        List<App> recentApps = appDao.find(chain);
        System.out.println("Found " + recentApps.size() + " recent admin apps");

        System.out.println("Query conditions test completed");
    }
}
