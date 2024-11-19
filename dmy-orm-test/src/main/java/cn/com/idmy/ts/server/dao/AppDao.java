package cn.com.idmy.ts.server.dao;

import cn.com.idmy.orm.core.mybatis.MybatisDao;
import cn.com.idmy.ts.server.model.entity.App;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppDao extends MybatisDao<App, Long> {
}
