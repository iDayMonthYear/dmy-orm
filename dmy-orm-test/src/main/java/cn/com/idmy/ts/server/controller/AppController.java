package cn.com.idmy.ts.server.controller;

import cn.com.idmy.ts.server.model.entity.App;
import cn.com.idmy.ts.server.service.AppService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/ts/app")
@RequiredArgsConstructor
class AppController {
    private final AppService appService;

    @PostMapping("/all")
    public List<App> all() {
        return appService.all();
    }

    @PostMapping("/get/{id}")
    public App get(@PathVariable long id) {
        return appService.get(id);
    }

    @PostMapping("/find")
    public List<App> find(@RequestBody Collection<Long> ids) {
        return appService.find(ids);
    }


    @PostMapping("/crud")
    public Object testCrud() {
        return appService.testCrud();
    }

    @PostMapping("/batch")
    public String testBatch() {
        appService.testBatchOperations();
        return "Batch operations test completed";
    }

    @PostMapping("/conditions")
    public String testConditions() {
        appService.testQueryConditions();
        return "Query conditions test completed";
    }
}
