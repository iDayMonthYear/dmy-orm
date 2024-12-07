package cn.com.idmy.orm.mybatis;


import cn.com.idmy.orm.core.TableInfo;
import cn.com.idmy.orm.core.TableInfo.TableIdInfo;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;

import java.sql.Statement;

public class CustomIdGenerator implements KeyGenerator {
    protected final Configuration configuration;
    protected final TableInfo tableInfo;
    protected final TableIdInfo idInfo;
    protected IdGenerator idGenerator;

    public CustomIdGenerator(Configuration cfg, TableInfo info) {
        this.configuration = cfg;
        this.tableInfo = info;
        this.idInfo = info.id();
    }

    @Override
    public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {

    }

    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {

    }
}