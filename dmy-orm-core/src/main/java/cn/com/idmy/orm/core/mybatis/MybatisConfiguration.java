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
package cn.com.idmy.orm.core.mybatis;

import cn.com.idmy.orm.core.mybatis.handler.EnumTypeHandler;
import cn.com.idmy.orm.core.mybatis.handler.JSONArrayTypeHandler;
import cn.com.idmy.orm.core.mybatis.handler.JSONObjectTypeHandler;
import cn.com.idmy.orm.core.mybatis.handler.JsonTypeHandler;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.util.Map;

public class MybatisConfiguration extends Configuration {
    public MybatisConfiguration() {
        // 注册默认的类型处理器
        TypeHandlerRegistry registry = getTypeHandlerRegistry();
        // 枚举类型处理器
        registry.setDefaultEnumTypeHandler(EnumTypeHandler.class);
        // JSON类型处理器
        registry.register(JsonTypeHandler.class);
        registry.register(JSONObjectTypeHandler.class);
        registry.register(JSONArrayTypeHandler.class);
    }

    @Override
    public ParameterHandler newParameterHandler(MappedStatement ms, Object paramObj, BoundSql boundSql) {
        String id = ms.getId();
        if (!id.endsWith(SelectKeyGenerator.SELECT_KEY_SUFFIX)) {
            if (paramObj instanceof Map<?, ?> map && map.containsKey(MybatisConsts.SQL_PARAMS)) {
                PreparedParameterHandler handler = new PreparedParameterHandler(ms, paramObj, boundSql);
                return (ParameterHandler) interceptorChain.pluginAll(handler);
            }
        }
        return super.newParameterHandler(ms, paramObj, boundSql);
    }
}
