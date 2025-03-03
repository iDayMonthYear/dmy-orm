package cn.com.idmy.ts.server.controller;

import cn.com.idmy.base.model.Page;
import cn.com.idmy.ts.server.dao.AppDao;
import cn.com.idmy.ts.server.model.entity.App;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.text.StrUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 应用控制器，用于测试XML查询条件
 */
@Slf4j
@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
public class AppController {
    private final AppDao appDao;

    /**
     * 测试XML查询条件
     *
     * @param name    应用名称
     * @param orderBy 排序字段
     * @param desc    是否降序
     * @return 应用列表
     */
    @GetMapping("/list")
    public List<App> list(@RequestParam(required = false) String name,
                          @RequestParam(required = false) String orderBy,
                          @RequestParam(required = false, defaultValue = "false") boolean desc) {
        // 创建XML查询条件
        var query = appDao.xq()
                .like(StrUtil.isNotBlank(name), App::getName, "%" + name + "%");

        // 添加排序
        if (StrUtil.isNotBlank(orderBy)) {
            if ("name".equals(orderBy)) {
                query.orderBy(App::getName, desc);
            } else if ("createdAt".equals(orderBy)) {
                query.orderBy(App::getCreatedAt, desc);
            }
        }

        // 打印生成的条件字符串
        log.info("条件字符串: {}", query.getCond());
        log.info("排序字符串: {}", query.getOrderBy());
        log.info("分组字符串: {}", query.getGroupBy());

        // 使用XML中定义的方法执行查询
        return appDao.findByCondition(query);
    }

    /**
     * 测试XML查询条件分页
     *
     * @param pageNo   页码
     * @param pageSize 每页大小
     * @param name     应用名称
     * @param orderBy  排序字段
     * @param desc     是否降序
     * @return 分页结果
     */
    @GetMapping("/page")
    public Page<App> page(@RequestParam(defaultValue = "1") int pageNo,
                          @RequestParam(defaultValue = "10") int pageSize,
                          @RequestParam(required = false) String name,
                          @RequestParam(required = false) String orderBy,
                          @RequestParam(required = false, defaultValue = "false") boolean desc) {
        // 创建分页参数
        var page = Page.of(pageNo, pageSize);

        // 创建XML查询条件
        var query = appDao.xq()
                .like(StrUtil.isNotBlank(name), App::getName, "%" + name + "%");

        // 添加排序
        if (StrUtil.isNotBlank(orderBy)) {
            if ("name".equals(orderBy)) {
                query.orderBy(App::getName, desc);
            } else if ("createdAt".equals(orderBy)) {
                query.orderBy(App::getCreatedAt, desc);
            }
        }

        // 打印生成的条件字符串
        log.info("条件字符串: {}", query.getCond());
        log.info("排序字符串: {}", query.getOrderBy());
        log.info("分组字符串: {}", query.getGroupBy());

        // 使用XML中定义的方法执行分页查询
        return appDao.pageByCondition(page, query);
    }

    /**
     * 使用OrmDao内置的xmlList方法
     */
    @GetMapping("/xml-list")
    public List<App> xmlList(@RequestParam(required = false) String name) {
        // 创建XML查询条件
        var query = appDao.xq()
                .like(StrUtil.isNotBlank(name), App::getName, "%" + name + "%")
                .orderByDesc(App::getCreatedAt);

        // 使用OrmDao内置的xmlList方法
        return appDao.xmlList(query);
    }

    /**
     * 使用OrmDao内置的xmlPage方法
     */
    @GetMapping("/xml-page")
    public Page<App> xmlPage(@RequestParam(defaultValue = "1") int pageNo,
                             @RequestParam(defaultValue = "10") int pageSize,
                             @RequestParam(required = false) String name) {
        // 创建分页参数
        var page = Page.of(pageNo, pageSize);

        // 创建XML查询条件
        var query = appDao.xq()
                .like(StrUtil.isNotBlank(name), App::getName, "%" + name + "%")
                .orderByDesc(App::getCreatedAt);

        // 使用OrmDao内置的xmlPage方法
        return appDao.xmlPage(page, query);
    }
}
