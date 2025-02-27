module cn.com.idmy.orm {
    exports cn.com.idmy.orm; // 公开的包
    exports cn.com.idmy.orm.core; // 公开的包
    exports cn.com.idmy.orm.annotation; // 公开的包

    requires org.mybatis;
    requires cn.com.idmy.base;
    requires static lombok;
    requires org.dromara.hutool.core;
    requires org.slf4j;
    requires java.sql;
    requires com.alibaba.fastjson2;
    requires jakarta.validation;
    requires org.jetbrains.annotations;
}