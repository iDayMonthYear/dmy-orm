package cn.com.idmy.orm.core.query.ast;

import org.dromara.hutool.core.lang.Console;

public class Test {
    public static void main(String[] args) {
        Console.log(new Select(new Count())
                .from(Test.class.getSimpleName()).as("t")
                .where()
                .and().eq("a", "1").eq("b", "2")
                .or()
                .and().eq("c", "1").eq("d", "2")
                .groupBy("c", "b")
                .having("count(*) > 1")
                .orderBy("a", true)
                .sql());
    }
}