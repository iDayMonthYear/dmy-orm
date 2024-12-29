package cn.com.idmy.ts.server.service.impl;

import cn.com.idmy.base.model.Page;
import cn.com.idmy.orm.core.Query;
import cn.com.idmy.ts.server.dao.AppDao;
import cn.com.idmy.ts.server.model.entity.App;
import cn.com.idmy.ts.server.service.AppService;
import lombok.RequiredArgsConstructor;
import org.dromara.hutool.core.lang.Console;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AppServiceImpl implements AppService {
    private final AppDao dao;

    @Override
    public List<App> all() {
        return dao.all();
    }

    @Override
    public App get(Long id) {
        return dao.getNullable(id);
    }

    @Override
    public List<App> find(Collection<Long> ids) {
        return dao.find(ids);
    }

    @Override
    public Page<App> testCrud() {
//
//        // Test create
//        App newApp1 = App.builder()
//                .key("test-app")
//                .name("Test App")
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//        App newApp2 = App.builder()
//                .key("test-app")
//                .name("Test App")
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//        dao.inserts(List.of(newApp1, newApp2));
//
//        String str = dao.get(App::getName, 1222L);
//        System.err.println(str);
//
//        List<App> apps = dao.find(List.of(555L));
//        System.out.println(apps);
//
//        Long sum1 = dao.sum(App::getA, SelectChain.of(dao));
//        List<String> longs = dao.find(App::getKey, List.of(1L, 2L, 3L));
//        System.out.println(longs);
//        Map<Long, App> map = dao.map(1L, 2L, 3L);
//        System.out.println(map);
//        Map<String, App> map1 = dao.map(App::getKey, SelectChain.of(dao));
//        System.out.println(map1);
//        long count = dao.count(SelectChain.of(dao));
//        System.out.println(count);
//
        Console.error(dao.find(Query.of(dao).endsWith(App::name, "票")));
//        Console.error(dao.find(Selects.of(dao).between(App::getId, 1, 10)));
//        Page<App> pageIn = Page.of(1, 2);
//        pageIn.setParams(new App());
//        pageIn.setSorts(new String[]{"key", "desc"});
//        Page<App> page = dao.page(pageIn, Selects.of(dao).between(App::getId, 1, 100));
//        Console.error(page);

//        Map<Integer, Long> map = new HashMap<>();
//        map.put(1, 2L);
//        dao.update(Updates.of(dao).set(App::getJson2, map).eq(App::getId, 1L));
//        App app = dao.get(1L);
//        Query<App> select = Query.of(dao).eq(App::id, 1).eq(App::creatorId, c -> c.plus(1L));
//        dao.find(select);
//        Query<App> select = Query.of(dao)
//                .select(() -> SqlFn.min(App::id)).eq(App::creatorId, 1L).eq(App::creatorId,  c -> c.plus(1L));


        Query<App> select = Query.of(dao)
                .select(c -> c.max(App::id))
                .between(App::createdAt, LocalDateTime.now().minusDays(30), LocalDateTime.now())
                .eq(App::id, c -> c.plus(1L))
                .eq(App::id, "43423");

        dao.find(select);
        List<App> apps = new ArrayList<>();
        apps.add(App.builder().id(null).key("1").build());
        apps.add(App.builder().key("2").build());

        dao.create(apps.getFirst());
        dao.update(App.builder().id(3344L).key(System.currentTimeMillis() + "").build(), false);
        return null;
    }

    @Override
    public void testBatchOperations() {
        System.out.println("=== Testing batch operations ===");

        // Test batch query
        List<App> apps = find(List.of(1L, 2L));
        System.out.println("Found " + apps.size() + " apps in batch query");

        // Test using SelectChain
        Query<App> chain = Query.of(dao);
        chain.in(App::id, 1, 2);
        List<App> chainResult = dao.find(chain);
        System.out.println("Found " + chainResult.size() + " apps using SelectChain");

        System.out.println("Batch operations test completed");
    }

    @Override
    public void testQueryConditions() {
        System.out.println("=== Testing query conditions ===");

        // Test different query conditions
        Query<App> chain = Query.of(dao);

        // Test like condition
        List<App> adminApps = dao.find(chain);
        System.out.println("Found " + adminApps.size() + " admin apps");

        // Test multiple conditions
        chain = Query.of(dao);
        chain.in(App::key, "dmy-ts-admin")
                .gt(App::createdAt, LocalDateTime.now().minusDays(30));
        List<App> recentApps = dao.find(chain);
        System.out.println("Found " + recentApps.size() + " recent admin apps");

        System.out.println("Query conditions test completed");
    }
}
