package cn.com.idmy.orm.core.mybatis;


import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
class ProviderUtil {

    private ProviderUtil() {
    }

    public static void setSqlArgs(Map<String, Object> params, List<Object> args) {
        params.put(MybatisConsts.SQL_ARGS, args);
    }
}
