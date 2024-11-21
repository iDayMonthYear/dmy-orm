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
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AppServiceImpl implements AppService {
    private final AppDao dao;

    @Override
    public List<App> all() {
        SelectChain<App> chain = SelectChain.of(dao);
        chain.in(App::getKey, "dmy-ts-admin", "saas-invoice-admin");
        return dao.find(chain);
    }

    @Override
    public App get(Long id) {
        return dao.get(id);
    }

    @Override
    public List<App> find(Collection<Long> ids) {
        return dao.find(ids);
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
        dao.inserts(List.of(newApp1, newApp2));

        String str = dao.get(App::getName, 1222L);
        System.err.println(str);

        List<App> apps = dao.find(List.of(555L));
        System.out.println(apps);

        Long sum1 = dao.sum(App::getA, SelectChain.of(dao));
        List<String> longs = dao.find(App::getKey, List.of(1L, 2L, 3L));
        System.out.println(longs);
        Map<Long, App> map = dao.map(1L, 2L, 3L);
        System.out.println(map);
        Map<String, App> map1 = dao.map(App::getKey, SelectChain.of(dao));
        System.out.println(map1);
        long count = dao.count(SelectChain.of(dao));
        System.out.println(count);
    }

    @Override
    public void testBatchOperations() {
        System.out.println("=== Testing batch operations ===");

        // Test batch query
        List<App> apps = find(List.of(1L, 2L));
        System.out.println("Found " + apps.size() + " apps in batch query");

        // Test using SelectChain
        SelectChain<App> chain = SelectChain.of(dao);
        chain.in(App::getId, 1, 2);
        List<App> chainResult = dao.find(chain);
        System.out.println("Found " + chainResult.size() + " apps using SelectChain");

        System.out.println("Batch operations test completed");
    }

    @Override
    public void testQueryConditions() {
        System.out.println("=== Testing query conditions ===");

        // Test different query conditions
        SelectChain<App> chain = SelectChain.of(dao);

        // Test like condition
        List<App> adminApps = dao.find(chain);
        System.out.println("Found " + adminApps.size() + " admin apps");

        // Test multiple conditions
        chain = SelectChain.of(dao);
        chain.in(App::getKey, "dmy-ts-admin")
                .gt(App::getCreatedAt, LocalDateTime.now().minusDays(30));
        List<App> recentApps = dao.find(chain);
        System.out.println("Found " + recentApps.size() + " recent admin apps");

        System.out.println("Query conditions test completed");
    }
}
