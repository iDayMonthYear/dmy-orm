module cn.com.idmy.orm {
    exports cn.com.idmy.orm.core; // 公开的包
    exports cn.com.idmy.orm.annotation; // 公开的包

    requires lombok;
    requires org.jetbrains.annotations;
    requires org.slf4j;
    requires dmy.base;
    requires org.mybatis;
    requires org.dromara.hutool.core;
    requires jakarta.validation;
    requires java.sql;
    requires com.alibaba.fastjson2;
}