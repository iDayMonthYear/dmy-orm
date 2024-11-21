package cn.com.idmy.ts.server.dao;

import cn.com.idmy.orm.core.MybatisDao;
import cn.com.idmy.ts.server.model.entity.TaskLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskLogDao extends MybatisDao<TaskLog, Long> {
}
