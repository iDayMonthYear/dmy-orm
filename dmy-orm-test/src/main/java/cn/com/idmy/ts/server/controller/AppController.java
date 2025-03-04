package cn.com.idmy.ts.server.controller;

import cn.com.idmy.ts.server.dao.AppDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 应用控制器，用于测试XML查询条件
 */
@Slf4j
@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
public class AppController {
    private final AppDao appDao;
}
