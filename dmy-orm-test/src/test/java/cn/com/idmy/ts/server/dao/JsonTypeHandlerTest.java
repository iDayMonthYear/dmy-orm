package cn.com.idmy.ts.server.dao;

import cn.com.idmy.orm.core.SelectChain;
import cn.com.idmy.orm.core.UpdateChain;
import cn.com.idmy.ts.server.model.entity.JsonTestEntity;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Transactional
class JsonTypeHandlerTest {

    @Autowired
    private JsonTestDao jsonTestDao;

    @Test
    void testJSONObjectTypeHandler() {
        // 创建测试数据
        JSONObject jsonObject = new JSONObject()
            .fluentPut("name", "test")
            .fluentPut("age", 18)
            .fluentPut("nested", new JSONObject().fluentPut("key", "value"));

        JsonTestEntity entity = new JsonTestEntity()
            .setJsonData(jsonObject);
            
        // 测试插入
        jsonTestDao.insert(entity);
        
        // 测试查询
        JsonTestEntity found = jsonTestDao.get(entity.getId());
        assertNotNull(found.getJsonData());
        assertEquals("test", found.getJsonData().getString("name"));
        assertEquals(18, found.getJsonData().getIntValue("age"));
        assertEquals("value", found.getJsonData().getJSONObject("nested").getString("key"));
        
        // 测试更新
        jsonObject.put("name", "updated");
        jsonTestDao.update(
            UpdateChain.of(jsonTestDao)
                .set(JsonTestEntity::getJsonData, jsonObject)
                .eq(JsonTestEntity::getId, entity.getId())
        );
        
        found = jsonTestDao.get(entity.getId());
        assertEquals("updated", found.getJsonData().getString("name"));
    }

    @Test
    void testJSONArrayTypeHandler() {
        // 创建测试数据
        JSONArray jsonArray = new JSONArray()
            .fluentAdd(new JSONObject().fluentPut("id", 1).fluentPut("name", "item1"))
            .fluentAdd(new JSONObject().fluentPut("id", 2).fluentPut("name", "item2"));

        JsonTestEntity entity = new JsonTestEntity()
            .setArrayData(jsonArray);
            
        // 测试插入
        jsonTestDao.insert(entity);
        
        // 测试查询
        JsonTestEntity found = jsonTestDao.get(entity.getId());
        assertNotNull(found.getArrayData());
        assertEquals(2, found.getArrayData().size());
        assertEquals("item1", found.getArrayData().getJSONObject(0).getString("name"));
        assertEquals("item2", found.getArrayData().getJSONObject(1).getString("name"));
        
        // 测试更新
        jsonArray.add(new JSONObject().fluentPut("id", 3).fluentPut("name", "item3"));
        jsonTestDao.update(
            UpdateChain.of(jsonTestDao)
                .set(JsonTestEntity::getArrayData, jsonArray)
                .eq(JsonTestEntity::getId, entity.getId())
        );
        
        found = jsonTestDao.get(entity.getId());
        assertEquals(3, found.getArrayData().size());
    }

    @Test
    void testJsonStringTypeHandler() {
        // 创建测试数据
        String jsonString = "{\"key\":\"value\",\"number\":123}";
        
        JsonTestEntity entity = new JsonTestEntity()
            .setJsonString(jsonString);
            
        // 测试插入
        jsonTestDao.insert(entity);
        
        // 测试查询
        JsonTestEntity found = jsonTestDao.get(entity.getId());
        assertNotNull(found.getJsonString());
        JSONObject parsed = JSONObject.parseObject(found.getJsonString());
        assertEquals("value", parsed.getString("key"));
        assertEquals(123, parsed.getIntValue("number"));
    }

    @Test
    void testNullJsonValues() {
        // 测试null值处理
        JsonTestEntity entity = new JsonTestEntity()
            .setJsonData(null)
            .setArrayData(null)
            .setJsonString(null);
            
        jsonTestDao.insert(entity);
        
        JsonTestEntity found = jsonTestDao.get(entity.getId());
        assertNull(found.getJsonData());
        assertNull(found.getArrayData());
        assertNull(found.getJsonString());
    }

    @Test
    void testComplexQuery() {
        // 创建多条测试数据
        for (int i = 0; i < 3; i++) {
            JSONObject jsonObject = new JSONObject()
                .fluentPut("id", i)
                .fluentPut("name", "test" + i);
                
            JsonTestEntity entity = new JsonTestEntity()
                .setJsonData(jsonObject);
                
            jsonTestDao.insert(entity);
        }
        
        // 测试查询
        List<JsonTestEntity> entities = jsonTestDao.find(
            SelectChain.of(jsonTestDao)
                .select(JsonTestEntity::getId, JsonTestEntity::getJsonData)
        );
        
        assertEquals(3, entities.size());
        entities.forEach(e -> {
            assertNotNull(e.getJsonData());
            assertTrue(e.getJsonData().containsKey("name"));
        });
    }
} 