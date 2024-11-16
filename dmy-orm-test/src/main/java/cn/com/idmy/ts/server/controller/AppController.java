package cn.com.idmy.ts.server.controller;

import cn.com.idmy.ts.server.model.entity.App;
import cn.com.idmy.ts.server.service.AppService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ts/app")
@RequiredArgsConstructor
class AppController {
    private final AppService appService;

    @PostMapping("/get")
    public List<App> get() {
        return appService.all();
    }
}
