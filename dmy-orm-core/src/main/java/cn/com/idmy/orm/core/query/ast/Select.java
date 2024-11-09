package cn.com.idmy.orm.core.query.ast;

import cn.com.idmy.orm.core.query.OrmDao;
import cn.com.idmy.orm.core.query.test.User;
import cn.com.idmy.orm.core.query.test.UserDao;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true, chain = false)
public class Select<T> extends RootNode implements Crud {
    protected RootNode root;
    protected Class<T> table;

    Select(Class<T> table) {
        root = this;
        this.table = table;
    }

    public static <T> Select<T> of(OrmDao<T> dao) {
        return new Select<>(dao.entityType());
    }

    public SelectField<T, Select<T>> select() {
        return new SelectField<>(this, "*");
    }

    public SelectField<T, Select<T>> select(String... field) {
        return new SelectField<>(this, field);
    }

    @SafeVarargs
    public final SelectField<T, Select<T>> select(FieldGetter<T, ?>... fields) {
        return new SelectField<>(this, fields);
    }

    public static void main(String[] args) {
        UserDao dao = () -> User.class;
        Where<User, Select<User>> where = Select.of(dao).select(User::id, User::name).from().where();
        Condition<User, Select<User>> eq = where.eq(User::id, 1);
        Condition<User, Select<User>> eq = where.eq(User::id, 1);
        Select<User> semi = eq.semi();


    }
}
