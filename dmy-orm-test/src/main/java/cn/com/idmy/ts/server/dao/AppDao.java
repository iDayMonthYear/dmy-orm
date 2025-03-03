package cn.com.idmy.ts.server.dao;

import cn.com.idmy.base.model.Page;
import cn.com.idmy.orm.core.OrmDao;
import cn.com.idmy.orm.core.XmlQuery;
import cn.com.idmy.ts.server.model.entity.App;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AppDao extends OrmDao<App, Long[]> {
    /**
     * 使用XML查询条件查询应用
     *
     * @param query XML查询条件
     * @return 应用列表
     */
    List<App> findByCondition(XmlQuery<App, Long[]> query);

    /**
     * 使用XML查询条件分页查询应用
     *
     * @param page  分页参数
     * @param query XML查询条件
     * @return 分页结果
     */
    Page<App> pageByCondition(Page<?> page, XmlQuery<App, Long[]> query);
}
