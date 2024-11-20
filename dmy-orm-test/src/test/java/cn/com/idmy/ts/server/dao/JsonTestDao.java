package cn.com.idmy.ts.server.dao;

import cn.com.idmy.orm.mybatis.MybatisDao;
import cn.com.idmy.ts.server.model.entity.JsonTestEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JsonTestDao extends MybatisDao<JsonTestEntity, Long> {
} 