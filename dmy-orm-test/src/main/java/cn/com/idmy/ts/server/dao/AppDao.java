package cn.com.idmy.ts.server.dao;

import cn.com.idmy.orm.core.OrmDao;
import cn.com.idmy.ts.server.model.entity.App;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppDao extends OrmDao<App, Long[]> {
}
