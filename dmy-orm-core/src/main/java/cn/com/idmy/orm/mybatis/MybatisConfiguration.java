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
        var registry = getTypeHandlerRegistry();
        registry.setDefaultEnumTypeHandler(EnumTypeHandler.class);
        registry.register(JsonTypeHandler.class);
        registry.register(JsonObjectTypeHandler.class);
        registry.register(JsonArrayTypeHandler.class);
    }

    @Override
    public ParameterHandler newParameterHandler(MappedStatement ms, Object param, BoundSql boundSql) {
        var msId = ms.getId();
        if (!msId.endsWith(SelectKeyGenerator.SELECT_KEY_SUFFIX)) {
            if (param instanceof Map<?, ?> map && map.containsKey(MybatisConsts.SQL_PARAMS)) {
                var handler = new MybatisParameterHandler(ms, param, boundSql);
                return (ParameterHandler) interceptorChain.pluginAll(handler);
            }
        }
        return super.newParameterHandler(ms, param, boundSql);
    }

    @Override
    public void addMappedStatement(MappedStatement ms) {
        if (ms.getKeyGenerator() == NoKeyGenerator.INSTANCE) {
            String msId = ms.getId();
            if (msId.endsWith(MybatisConsts.CREATE) || msId.endsWith(MybatisConsts.CREATES)) {
                ms = replaceEntityIdGenerator(ms);
            }
        }
        super.addMappedStatement(ms);
    }

    private MappedStatement replaceEntityIdGenerator(MappedStatement ms) {
        var tableInfo = getTableInfo(ms);
        if (tableInfo == null) {
            return ms;
        }

        var idGenerator = MybatisIdGeneratorUtil.create(ms, tableInfo);
        if (idGenerator == NoKeyGenerator.INSTANCE) {
            return ms;
        }

        if (ms.getId().endsWith(MybatisConsts.CREATES)) {
            idGenerator = new EntitiesIdGenerator(idGenerator);
        }

        return new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), ms.getSqlSource(), ms.getSqlCommandType())
                .resource(ms.getResource())
                .fetchSize(ms.getFetchSize())
                .timeout(ms.getTimeout())
                .statementType(ms.getStatementType())
                .keyGenerator(idGenerator) // 替换主键生成器
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
