package cn.com.idmy.orm.core.provider;


import cn.com.idmy.orm.core.ast.DeleteChain;
import cn.com.idmy.orm.core.ast.SelectChain;
import cn.com.idmy.orm.core.ast.UpdateChain;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.builder.annotation.ProviderContext;


@SuppressWarnings({"rawtypes", "DuplicatedCode"})
public class MybatisSqlProvider {
    private MybatisSqlProvider() {
    }

    public String get(ProviderContext context, @Param("chain") SelectChain<?> chain) {
        return chain.sql();
    }

    public String find(ProviderContext context, @Param("chain") SelectChain<?> chain) {
        return chain.sql();
    }

    public String update(ProviderContext context, @Param("chain") UpdateChain<?> chain) {
        return chain.sql();
    }

    public String delete(ProviderContext context, @Param("chain") DeleteChain<?> chain) {
        return chain.sql();
    }
}
