package cn.com.idmy.orm.core.query.ast;

public class Test {
    public static void main(String[] args) {
        Select select = new Select(new Count(), new Avg(), new Max(), new Min(), new Sum(), new Column("name"));
        select.from().where();
    }
}
