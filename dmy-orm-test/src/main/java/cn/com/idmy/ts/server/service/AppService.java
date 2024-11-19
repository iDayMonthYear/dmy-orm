package cn.com.idmy.ts.server.service;


import cn.com.idmy.ts.server.model.entity.App;

import java.util.Collection;
import java.util.List;

public interface AppService  {
    List<App> all();

    App get(String key);

    List<App> find(Collection<String> ids);
}
