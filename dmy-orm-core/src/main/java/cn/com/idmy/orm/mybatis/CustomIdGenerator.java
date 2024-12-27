package cn.com.idmy.orm.mybatis;


import cn.com.idmy.orm.core.TableInfo;
import cn.com.idmy.orm.core.TableInfo.TableId;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.jetbrains.annotations.NotNull;

import java.sql.Statement;

public class CustomIdGenerator implements KeyGenerator {
    protected final Configuration configuration;
    protected final TableInfo table;
    protected final TableId id;
    protected IdGenerator idGenerator;

    public CustomIdGenerator(@NotNull Configuration cfg, @NotNull TableInfo table) {
        this.configuration = cfg;
        this.table = table;
        this.id = table.id();
    }

    @Override
    public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {

    }

    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {

    }
}