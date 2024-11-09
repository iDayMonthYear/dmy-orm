package cn.com.idmy.orm.core.query.ast;

public abstract class AbstractCondition<T, CRUD extends Crud> {
    protected final CRUD crud;

    protected AbstractCondition(CRUD crud) {
        this.crud = crud;
        crud.addNode(this);
    }

    public Condition<T, CRUD> eq(String column, Object value) {
        return new Condition<>(crud, Op.EQ, column, value); // 直接使用值
    }

    public Condition<T, CRUD> eq(FieldGetter<T, ?> column, Object value) {
        return new Condition<>(crud, Op.EQ, column, value); // 直接使用值
    }

    // 添加支持 SqlExpr 的 eq 方法
    public Condition<T, CRUD> eq(String column, SqlExpression value) {
        return new Condition<>(crud, Op.EQ, column, value); // 使用 SqlExpr
    }

    public Condition<T, CRUD> eq(FieldGetter<T, ?> column, SqlExpression value) {
        return new Condition<>(crud, Op.EQ, column, value); // 使用 SqlExpr
    }

    // 添加 ne 方法
    public Condition<T, CRUD> ne(String column, Object value) {
        return new Condition<>(crud, Op.NE, column, value); // 直接使用值
    }

    public Condition<T, CRUD> ne(FieldGetter<T, ?> column, Object value) {
        return new Condition<>(crud, Op.NE, column, value); // 直接使用值
    }

    public Condition<T, CRUD> ne(String column, SqlExpression value) {
        return new Condition<>(crud, Op.NE, column, value); // 使用 SqlExpr
    }

    public Condition<T, CRUD> ne(FieldGetter<T, ?> column, SqlExpression value) {
        return new Condition<>(crud, Op.NE, column, value); // 使用 SqlExpr
    }

    // 添加 lt 方法
    public Condition<T, CRUD> lt(String column, Object value) {
        return new Condition<>(crud, Op.LT, column, value); // 直接使用值
    }

    public Condition<T, CRUD> lt(FieldGetter<T, ?> column, Object value) {
        return new Condition<>(crud, Op.LT, column, value); // 直接使用值
    }

    public Condition<T, CRUD> lt(String column, SqlExpression value) {
        return new Condition<>(crud, Op.LT, column, value); // 使用 SqlExpr
    }

    public Condition<T, CRUD> lt(FieldGetter<T, ?> column, SqlExpression value) {
        return new Condition<>(crud, Op.LT, column, value); // 使用 SqlExpr
    }

    // 添加 le 方法
    public Condition<T, CRUD> le(String column, Object value) {
        return new Condition<>(crud, Op.LE, column, value); // 直接使用值
    }

    public Condition<T, CRUD> le(FieldGetter<T, ?> column, Object value) {
        return new Condition<>(crud, Op.LE, column, value); // 直接使用值
    }

    public Condition<T, CRUD> le(String column, SqlExpression value) {
        return new Condition<>(crud, Op.LE, column, value); // 使用 SqlExpr
    }

    public Condition<T, CRUD> le(FieldGetter<T, ?> column, SqlExpression value) {
        return new Condition<>(crud, Op.LE, column, value); // 使用 SqlExpr
    }

    // 添加 gt 方法
    public Condition<T, CRUD> gt(String column, Object value) {
        return new Condition<>(crud, Op.GT, column, value); // 直接使用值
    }

    public Condition<T, CRUD> gt(FieldGetter<T, ?> column, Object value) {
        return new Condition<>(crud, Op.GT, column, value); // 直接使用值
    }

    public Condition<T, CRUD> gt(String column, SqlExpression value) {
        return new Condition<>(crud, Op.GT, column, value); // 使用 SqlExpr
    }

    public Condition<T, CRUD> gt(FieldGetter<T, ?> column, SqlExpression value) {
        return new Condition<>(crud, Op.GT, column, value); // 使用 SqlExpr
    }

    // 添加 ge 方法
    public Condition<T, CRUD> ge(String column, Object value) {
        return new Condition<>(crud, Op.GE, column, value); // 直接使用值
    }

    public Condition<T, CRUD> ge(FieldGetter<T, ?> column, Object value) {
        return new Condition<>(crud, Op.GE, column, value); // 直接使用值
    }

    public Condition<T, CRUD> ge(String column, SqlExpression value) {
        return new Condition<>(crud, Op.GE, column, value); // 使用 SqlExpr
    }

    public Condition<T, CRUD> ge(FieldGetter<T, ?> column, SqlExpression value) {
        return new Condition<>(crud, Op.GE, column, value); // 使用 SqlExpr
    }

    // 添加 in 方法
    public Condition<T, CRUD> in(String column, Object... values) {
        return new Condition<>(crud, Op.IN, column, values); // 直接使用值
    }

    public Condition<T, CRUD> in(FieldGetter<T, ?> column, Object... values) {
        return new Condition<>(crud, Op.IN, column, values); // 直接使用值
    }

    public Condition<T, CRUD> in(String column, SqlExpression value) {
        return new Condition<>(crud, Op.IN, column, value); // 使用 SqlExpr
    }

    public Condition<T, CRUD> in(FieldGetter<T, ?> column, SqlExpression value) {
        return new Condition<>(crud, Op.IN, column, value); // 使用 SqlExpr
    }

    // 添加 notIn 方法
    public Condition<T, CRUD> notIn(String column, Object... values) {
        return new Condition<>(crud, Op.NOT_IN, column, values); // 直接使用值
    }

    public Condition<T, CRUD> notIn(FieldGetter<T, ?> column, Object... values) {
        return new Condition<>(crud, Op.NOT_IN, column, values); // 直接使用值
    }

    public Condition<T, CRUD> notIn(String column, SqlExpression value) {
        return new Condition<>(crud, Op.NOT_IN, column, value); // 使用 SqlExpr
    }

    public Condition<T, CRUD> notIn(FieldGetter<T, ?> column, SqlExpression value) {
        return new Condition<>(crud, Op.NOT_IN, column, value); // 使用 SqlExpr
    }

    // 添加 between 方法
    public Condition<T, CRUD> between(String column, Object start, Object end) {
        return new Condition<>(crud, Op.BETWEEN, column, new Object[]{start, end}); // 直接使用值
    }

    public Condition<T, CRUD> between(FieldGetter<T, ?> column, Object start, Object end) {
        return new Condition<>(crud, Op.BETWEEN, column, new Object[]{start, end}); // 直接使用值
    }

    public Condition<T, CRUD> between(String column, SqlExpression start, SqlExpression end) {
        return new Condition<>(crud, Op.BETWEEN, column, new SqlExpression[]{start, end}); // 使用 SqlExpr
    }

    public Condition<T, CRUD> between(FieldGetter<T, ?> column, SqlExpression start, SqlExpression end) {
        return new Condition<>(crud, Op.BETWEEN, column, new SqlExpression[]{start, end}); // 使用 SqlExpr
    }

    // 添加 like 方法
    public Condition<T, CRUD> like(String column, String pattern) {
        return new Condition<>(crud, Op.LIKE, column, pattern); // 直接使用值
    }

    public Condition<T, CRUD> like(FieldGetter<T, ?> column, String pattern) {
        return new Condition<>(crud, Op.LIKE, column, pattern); // 直接使用值
    }

    public Condition<T, CRUD> like(String column, SqlExpression pattern) {
        return new Condition<>(crud, Op.LIKE, column, pattern); // 使用 SqlExpr
    }

    public Condition<T, CRUD> like(FieldGetter<T, ?> column, SqlExpression pattern) {
        return new Condition<>(crud, Op.LIKE, column, pattern); // 使用 SqlExpr
    }

    // 添加 isNull 方法
    public Condition<T, CRUD> isNull(String column) {
        return new Condition<>(crud, Op.IS_NULL, column, null); // 直接使用值
    }

    public Condition<T, CRUD> isNull(FieldGetter<T, ?> column) {
        return new Condition<>(crud, Op.IS_NULL, column, null); // 直接使用值
    }

    // 添加 isNotNull 方法
    public Condition<T, CRUD> isNotNull(String column) {
        return new Condition<>(crud, Op.IS_NOT_NULL, column, null); // 直接使用值
    }

    public Condition<T, CRUD> isNotNull(FieldGetter<T, ?> column) {
        return new Condition<>(crud, Op.IS_NOT_NULL, column, null); // 直接使用值
    }
}