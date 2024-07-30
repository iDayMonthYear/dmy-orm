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

package cn.com.idmy.orm.processor.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TableInfo {

    /**
     * 实体类全类名。
     */
    private String entityName;

    /**
     * 实体类简单类名。
     */
    private String entitySimpleName;

    /**
     * 实体类注释。
     */
    private String entityComment;

    /**
     * 表名称。
     */
    private String tableName;

    /**
     * Schema 模式。
     */
    private String schema;

    public void setTableName(String tableName) {
        int indexOf = tableName.indexOf(".");
        if (indexOf > 0) {
            if (schema == null || schema.trim().isEmpty()) {
                this.schema = tableName.substring(0, indexOf);
                this.tableName = tableName.substring(indexOf + 1);
            } else {
                this.tableName = tableName;
            }
        } else {
            this.tableName = tableName;
        }
    }
}
