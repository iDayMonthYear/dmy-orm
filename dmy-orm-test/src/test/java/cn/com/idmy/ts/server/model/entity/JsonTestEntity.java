package cn.com.idmy.ts.server.model.entity;

import cn.com.idmy.orm.annotation.Table;
import cn.com.idmy.orm.annotation.TableId;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Table("ts_json_test")
public class JsonTestEntity {
    @TableId
    private Long id;
    
    private JSONObject jsonData;
    
    private JSONArray arrayData;
    
    private String jsonString;
} 