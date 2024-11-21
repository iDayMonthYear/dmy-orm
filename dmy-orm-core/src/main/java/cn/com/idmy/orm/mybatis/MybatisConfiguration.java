/*
 *  Copyright (c) 2022-2025, Mybatis-Flex (fuhai999@gmail.com).
 *  <p>
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package cn.com.idmy.orm.mybatis;

import cn.com.idmy.orm.mybatis.handler.EnumTypeHandler;
import cn.com.idmy.orm.mybatis.handler.JsonArrayTypeHandler;
import cn.com.idmy.orm.mybatis.handler.JsonObjectTypeHandler;
import cn.com.idmy.orm.mybatis.handler.JsonTypeHandler;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;

import java.util.Map;

import static cn.com.idmy.orm.core.TableManager.getTableInfo;

class MybatisConfiguration extends Configuration {
    public MybatisConfiguration() {
        // 注册默认的类型处理器
        var registry = getTypeHandlerRegistry();
        // 枚举类型处理器
        registry.setDefaultEnumTypeHandler(EnumTypeHandler.class);
        // JSON类型处理器
        registry.register(JsonTypeHandler.class);
        registry.register(JsonObjectTypeHandler.class);
        registry.register(JsonArrayTypeHandler.class);
    }

    @Override
    public ParameterHandler newParameterHandler(MappedStatement ms, Object param, BoundSql boundSql) {
        var id = ms.getId();
        if (!id.endsWith(SelectKeyGenerator.SELECT_KEY_SUFFIX)) {
            if (param instanceof Map<?, ?> map && map.containsKey(MybatisConsts.SQL_PARAMS)) {
                var handler = new MybatisParameterHandler(ms, param, boundSql);
                return (ParameterHandler) interceptorChain.pluginAll(handler);
            }
        }
        return super.newParameterHandler(ms, param, boundSql);
    }

    @Override
    public void addMappedStatement(MappedStatement ms) {
        if (ms.getId().endsWith(MybatisConsts.INSERT) || ms.getId().endsWith(MybatisConsts.INSERTS) && ms.getKeyGenerator() == NoKeyGenerator.INSTANCE) {
            ms = replaceEntityIdGenerator(ms);
        }
        super.addMappedStatement(ms);
    }

    private MappedStatement replaceEntityIdGenerator(MappedStatement ms) {
        var tableInfo = getTableInfo(ms);
        if (tableInfo == null) {
            return ms;
        }

        var keyGenerator = MybatisIdGeneratorUtil.create(ms, tableInfo);
        if (keyGenerator == NoKeyGenerator.INSTANCE) {
            return ms;
        }

        if (ms.getId().endsWith(MybatisConsts.INSERTS)) {
            keyGenerator = new EntitiesIdGenerator(keyGenerator);
        }

        return new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), ms.getSqlSource(), ms.getSqlCommandType())
                .resource(ms.getResource())
                .fetchSize(ms.getFetchSize())
                .timeout(ms.getTimeout())
                .statementType(ms.getStatementType())
                .keyGenerator(keyGenerator) // 替换主键生成器
                .keyProperty(MybatisConsts.ENTITY + "." + tableInfo.id().field().getName())
                .keyColumn(tableInfo.id().name())
                .databaseId(databaseId)
                .lang(ms.getLang())
                .resultOrdered(ms.isResultOrdered())
                .resultSets(ms.getResultSets() == null ? null : String.join(",", ms.getResultSets()))
                .resultMaps(ms.getResultMaps())
                .resultSetType(ms.getResultSetType())
                .flushCacheRequired(ms.isFlushCacheRequired())
                .useCache(ms.isUseCache())
                .cache(ms.getCache())
                .build();
    }
}
