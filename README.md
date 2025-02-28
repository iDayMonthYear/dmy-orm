# DMY-ORM

DMY-ORM 是一个轻量级的 ORM 框架，基于 MyBatis 构建，提供类型安全的链式查询、灵活的类型处理和强大的枚举支持。

## 目录

- [特性](#特性)
- [快速开始](#快速开始)
- [实体定义](#实体定义)
- [OrmDao 接口](#ormdao-接口)
- [查询操作](#查询操作)
- [更新操作](#更新操作)
- [删除操作](#删除操作)
- [条件构建](#条件构建)
- [安全机制](#安全机制)
- [高级功能](#高级功能)
    - [枚举支持](#枚举支持)
    - [ID生成策略](#id生成策略)
    - [类型处理器](#类型处理器)
    - [拦截器](#拦截器)
- [配置选项](#配置选项)
- [最佳实践](#最佳实践)
- [注意事项](#注意事项)
- [许可证](#许可证)

## 特性

- 类型安全的链式查询 API
- 自动类型转换
- JSON 字段处理
- 自定义 ID 生成策略
- Spring Boot 集成
- 数据库模式验证
- 枚举值处理
- SQL 拦截器支持
- 批量操作优化

## 快速开始

### Maven 依赖

```xml
<dependency>
    <groupId>cn.com.idmy</groupId>
    <artifactId>dmy-orm-spring-boot-starter</artifactId>
    <version>${version}</version>
</dependency>
```

### 基本用法示例

```java
// 查询
List<User> users = userDao.list(
    userDao.q()                                    // 使用简便方法 q()
        .select(User::getName, User::getStatus)
        .eq(User::getStatus, UserStatus.ACTIVE)
);

// 获取单条记录
User user = userDao.get(1L);

// 更新
int rows = userDao.update(
    userDao.u()                                    // 使用简便方法 u()
        .set(User::getStatus, UserStatus.INACTIVE)
        .eq(User::getId, 1)
);

// 删除
userDao.delete(
    userDao.d()                                    // 使用简便方法 d()
        .eq(User::getStatus, UserStatus.INACTIVE)
);
```

## 实体定义

实体类是与数据库表映射的 Java 对象，需要通过注解定义映射关系。

```java
@Table(value = "用户表", name = "t_user")
public class User {
    @Id(type = IdType.AUTO)
    private Long id;

    @Column(name = "user_name")
    private String name;
    
    private UserStatus status;
    private JSONObject jsonObject;
    private JSONArray jsonArray;
}
```

### 更多示例

```java
@Data
@Table(value = "订单表", name = "t_order")
public class Order {
    @Id(type = IdType.AUTO)                   // 自增ID
    private Long id;

    @Column(name = "order_no")                // 自定义列名
    private String orderNo;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "status")
    private OrderStatus status;               // 枚举类型

    @Column(name = "items")
    private JSONArray items;                  // JSON数组类型

    @Column(name = "buyer_info")
    private JSONObject buyerInfo;             // JSON对象类型

    @Column(name = "create_time")
    private LocalDateTime createTime;         // 日期时间类型

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "is_deleted")
    private Boolean deleted;                  // 逻辑删除字段
}
```

## OrmDao 接口

OrmDao 是框架中最常用的核心接口，它封装了所有数据库操作，是使用本框架的主要入口。

### DAO 接口定义

```java
public interface UserDao extends OrmDao<User, Long> {
    // 所有基本的 CRUD 操作均继承
}
```

### 核心方法概览

```java
// 实体和类型信息
Class<T> entityType();                       // 获取实体类型
Class<ID> idType();                          // 获取ID类型
TableInfo table();                           // 获取表信息

// 查询构建器快捷方法
Query<T, ID> q();                           // 创建查询构建器 (nullable = true)
Query<T, ID> q(boolean nullable);
Update<T, ID> u();                          // 创建更新构建器 (nullable = false)
Update<T, ID> u(boolean nullable);
Delete<T, ID> d();                          // 创建删除构建器 (nullable = false)
Delete<T, ID> d(boolean nullable);

// 查询操作
long count(Query<T, ID> q);                  // 统计查询
boolean exists(Query<T, ID> q);              // 存在性检查
boolean exists(ID id);
List<T> list(Query<T, ID> q);                // 列表查询
List<T> list(Collection<ID> ids);
T get(Query<T, ID> q);                       // 单条记录查询
T get(ID id);

// 增删改操作
int create(T entity);                        // 创建记录
int creates(Collection<T> entities);         // 批量创建
int update(Update<T, ID> update);            // 更新记录
int update(T entity);
int delete(Delete<T, ID> d);                 // 删除记录
int delete(ID id);
```

## 查询操作

### 基本查询

```java
// 根据ID查询
User user = userDao.get(1L);

// 条件查询
List<User> activeUsers = userDao.list(
    userDao.q()
        .eq(User::getStatus, UserStatus.ACTIVE)
        .orderBy(User::getCreateTime, false)
);

// 字段投影
String userName = userDao.get(User::getName, 1L);

// 条件存在性检查
boolean exists = userDao.exists(
    userDao.q()
        .eq(User::getEmail, "test@example.com")
);
```

### 条件组合

```java
// 多条件组合查询
List<Order> orders = orderDao.list(
    orderDao.q()
        .select(Order::getId, Order::getOrderNo, Order::getTotalAmount, Order::getStatus)
        .eq(Order::getStatus, OrderStatus.PAID)
        .gt(Order::getTotalAmount, new BigDecimal("100"))
        .between(Order::getCreateTime, 
                LocalDateTime.now().minusDays(7), 
                LocalDateTime.now())
        .orderBy(Order::getCreateTime, false)
);

// OR条件查询
List<Order> orders = orderDao.list(
    orderDao.q()
        .eq(Order::getStatus, OrderStatus.COMPLETED)
        .or(w -> w
            .eq(Order::getStatus, OrderStatus.CANCELLED)
            .lt(Order::getCreateTime, LocalDateTime.now().minusDays(30))
        )
);
```

### 分页与排序

```java
// 分页查询
int pageSize = 20;
int pageNum = 1;
List<Order> orders = orderDao.list(
    orderDao.q()
        .eq(Order::getStatus, OrderStatus.PENDING)
        .orderBy(Order::getCreateTime, false)
        .limit(pageSize)
        .offset((pageNum - 1) * pageSize)
);

// 使用Page对象
Page<Order> orderPage = orderDao.page(
    new Page<>(1, 20),
    orderDao.q()
        .orderBy(Order::getCreateTime, false)
);
```

### 聚合查询

```java
// COUNT
long count = userDao.count(Query.of(User.class));

// 统计查询
Map<OrderStatus, Long> statusCount = orderDao.groupCount(
    Order::getStatus,
    orderDao.q()
        .gt(Order::getCreateTime, LocalDateTime.now().minusDays(1))
);

// 聚合函数
BigDecimal total = userDao.sum(
    Order::getTotalAmount,
    orderDao.q()
        .eq(Order::getStatus, OrderStatus.COMPLETED)
);

Double avg = userDao.avg(User::getScore, Query.of(User.class));
Integer max = userDao.max(User::getAge, Query.of(User.class));
Integer min = userDao.min(User::getAge, Query.of(User.class));
```

### SQL 函数支持

```java
Query.of(User.class)
    .select(SqlFn.concat(User::getFirstName, " ", User::getLastName))
    .select(SqlFn.substring(User::getName, 1, 3))
    .select(SqlFn.date(User::getCreateTime));
```

## 更新操作

### 基本更新

```java
// 实体更新（根据ID）
User user = userDao.get(1L);
user.setStatus(UserStatus.INACTIVE);
userDao.update(user);                                  // 更新所有非空字段
userDao.update(user, false);                           // 更新所有字段，包括null

// 条件更新
int updated = orderDao.update(
    orderDao.u()
        .set(Order::getStatus, OrderStatus.CANCELLED)
        .set(Order::getUpdateTime, LocalDateTime.now())
        .eq(Order::getStatus, OrderStatus.PENDING)
        .lt(Order::getCreateTime, LocalDateTime.now().minusHours(24))
);
```

### 批量更新

```java
// 批量更新
List<Long> orderIds = Arrays.asList(1L, 2L, 3L);
int updated = orderDao.update(
    orderDao.u()
        .set(Order::getStatus, OrderStatus.SHIPPED)
        .set(Order::getUpdateTime, LocalDateTime.now())
        .in(Order::getId, orderIds)
);

// 批量实体更新
List<User> users = Arrays.asList(user1, user2, user3);
int[] results = userDao.update(users, 100, true);      // 批量更新，每批100条
```

### 创建或更新

```java
// 创建
User newUser = new User();
newUser.setName("张三");
newUser.setStatus(UserStatus.ACTIVE);
userDao.create(newUser);                               // 创建后自动设置ID

// ID检查并更新或创建
User user = new User();
user.setId(1L);
user.setName("李四");
userDao.createOrUpdate(user);                          // 存在则更新，不存在则创建
userDao.createOrUpdate(user, true);                    // 忽略null值
```

## 删除操作

### 基本删除

```java
// 按ID删除
userDao.delete(1L);

// 批量删除
userDao.delete(Arrays.asList(1L, 2L, 3L));

// 条件删除
int deleted = orderDao.delete(
    orderDao.d()
        .eq(Order::getStatus, OrderStatus.CANCELLED)
        .lt(Order::getCreateTime, LocalDateTime.now().minusMonths(6))
);
```

### 逻辑删除

```java
// 逻辑删除（通过更新操作）
int deleted = orderDao.update(
    orderDao.u()
        .set(Order::getDeleted, true)
        .set(Order::getUpdateTime, LocalDateTime.now())
        .eq(Order::getStatus, OrderStatus.CANCELLED)
);
```

## 条件构建

### 基本条件

```java
userDao.q()
    .eq(User::getStatus, status)      // 等于
    .ne(User::getStatus, status)      // 不等于
    .gt(User::getAge, 18)            // 大于
    .ge(User::getAge, 18)            // 大于等于
    .lt(User::getAge, 60)            // 小于
    .le(User::getAge, 60)            // 小于等于
    .like(User::getName, "张")        // LIKE '%张%'
    .startsWith(User::getName, "张")  // LIKE '张%'
    .endsWith(User::getName, "张")    // LIKE '%张'
    .in(User::getId, ids)            // IN (1,2,3)
    .notIn(User::getId, ids)         // NOT IN
    .isNull(User::getDeleteTime)     // IS NULL
    .isNotNull(User::getUpdateTime)  // IS NOT NULL
    .between(User::getAge, 18, 60)   // BETWEEN
```

### 组合条件

```java
userDao.q()
    .eq(User::getStatus, UserStatus.ACTIVE)
    .or(w -> w                       // OR 条件
        .eq(User::getStatus, UserStatus.PENDING)
        .gt(User::getCreateTime, LocalDateTime.now().minusDays(1))
    )
    .and(w -> w                      // AND 条件组
        .gt(User::getAge, 18)
        .lt(User::getAge, 60)
    );
```

### 子查询

```java
// 子查询条件
List<Product> products = productDao.list(
    productDao.q()
        .gt(Product::getStock, 0)
        .in(Product::getId, 
            orderDao.q()
                .select(Order::getProductId)
                .eq(Order::getStatus, OrderStatus.COMPLETED)
        )
);
```

### 字段选择

```java
// 选择特定字段
userDao.q()
    .select(User::getId, User::getName, User::getStatus);

// 排除特定字段
userDao.q()
    .excludeSelect(User::getPassword, User::getSalt);
```

## 安全机制

### nullable 参数

`nullable` 参数用于控制条件值为空时的行为。不同操作类型的默认值不同：

- Query：默认 `nullable = true`，适合动态查询场景
- Update：默认 `nullable = false`，防止意外的更新操作
- Delete：默认 `nullable = false`，防止意外的删除操作

```java
// 查询：默认允许空值
List<User> users = userDao.list(
    userDao.q()    // nullable = true
        .eq(User::getName, name)          // name 为 null 时跳过此条件
        .gt(User::getAge, minAge)         // minAge 为 null 时跳过此条件
);

// 更新：默认不允许空值
int updated = userDao.update(
    userDao.u()    // nullable = false
        .set(User::getStatus, status)
        .eq(User::getId, id)              // id 为 null 时抛出异常
);

// 显式指定 nullable 值
userDao.q(true)   // 允许空值的查询
userDao.u(true)   // 允许空值的更新
userDao.d(true)   // 允许空值的删除
```

### 全空条件处理

当查询条件中设置 `nullable=true` 且实际上所有条件都为空值且 `force=false` 时，框架会采取特殊处理，以防止意外的全表扫描：

```java
// 当所有条件都为null时的行为
String name = null;
Integer age = null;

// 对于单条记录查询，返回null
User user = userDao.get(
        userDao.q()
                .eq(User::getName, name)      // name为null，条件被跳过
                .gt(User::getAge, age)        // age为null，条件被跳过
);  // 结果：没有任何有效条件且force=false，直接返回null

// 对于列表查询，返回空列表
List<User> users = userDao.list(
        userDao.q()
                .eq(User::getName, name)      // name为null，条件被跳过
                .gt(User::getAge, age)        // age为null，条件被跳过
);  // 结果：没有任何有效条件且force=false，直接返回空列表

// 如果需要执行全表查询，必须使用force()
List<User> allUsers = userDao.list(
        userDao.q()
                .force()                      // 显式声明允许无条件查询
);
```

这种设计可以有效防止因条件值全为空导致的意外全表查询，提高了查询操作的安全性。

### force 参数

`force` 参数用于控制无条件更新/删除/查询的安全机制：

```java
// 默认情况：无条件更新会抛出异常
userDao.update(
    userDao.u()
        .set(User::getStatus, UserStatus.INACTIVE)
        // 异常：更新语句没有条件！可使用 force 强制执行
);

// 使用 force() 强制执行全表更新
userDao.update(
    userDao.u()
        .force()    // 标记为强制执行
        .set(User::getStatus, UserStatus.INACTIVE)
);
```

### 最佳实践

- 查询操作：默认允许空值，适合动态查询场景
- 更新操作：默认不允许空值，确保更新条件的有效性
- 删除操作：默认不允许空值，防止误删数据
- 谨慎使用 `force()`，添加注释说明原因
- 通过拦截器对 `force()` 操作进行权限检查或日志记录

## 高级功能

### 枚举支持

框架提供了强大的枚举类型支持，通过实现 `IEnum` 接口来实现：

```java
public interface IEnum<T> {
    @NotNull
    String title();    // 枚举项的标题/显示名称

    @NotNull
    T value();        // 枚举项的值

    @Nullable
    default String color() {  // 可选的颜色属性
        return null;
    }
}
```

实现示例：

```java
public enum OrderStatus implements IEnum<Integer> {
    PENDING(0, "待支付", "#999999"),
    PAID(1, "已支付", "#3399FF"),
    SHIPPED(2, "已发货", "#66CC00"),
    COMPLETED(3, "已完成", "#00CC33"),
    CANCELLED(-1, "已取消", "#FF3333");

    private final Integer value;
    private final String title;
    private final String color;

    OrderStatus(Integer value, String title, String color) {
        this.value = value;
        this.title = title;
        this.color = color;
    }

    @NotNull
    @Override
    public String title() {
        return title;
    }

    @NotNull
    @Override
    public Integer value() {
        return value;
    }

    @Nullable
    @Override
    public String color() {
        return color;
    }

    public static OrderStatus fromValue(Integer value) {
        return Arrays.stream(values())
            .filter(status -> status.value().equals(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid order status value: " + value));
    }
}
```

使用示例：

```java
// 获取枚举信息
OrderStatus status = OrderStatus.PAID;
String title = status.title();     // "已支付"
Integer value = status.value();    // 1
String color = status.color();     // "#3399FF"

// 在实体类中使用
@Column(name = "status")
private OrderStatus status;  // 枚举类型字段

// 查询示例
List<Order> orders = orderDao.list(
    orderDao.q()
        .eq(Order::getStatus, OrderStatus.PAID)  // 使用枚举值作为查询条件
);
```

### ID生成策略

框架支持以下几种 ID 生成策略：

```java
public enum IdType {
    DEFAULT,    // 默认主键生成策略，可以在全局配置中指定为其他类型
    AUTO,       // 自动生成主键（如自增）
    GENERATOR,  // 使用生成器生成主键
    SEQUENCE,   // 使用序列生成主键
    NONE        // 不使用主键
}
```

使用示例：

```java
// 使用自增主键
@Table(name = "t_order")
public class Order {
    @Id(type = IdType.AUTO)
    private Long id;
}

// 使用自定义生成器
@Table(name = "t_product")
public class Product {
    @Id(type = IdType.GENERATOR)
    private String id;  // 可以是任意类型，由生成器决定
}
```

### 类型处理器

```java
public class ListTypeHandler extends BaseTypeHandler<List<String>> {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) 
            throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return fromJson(rs.getString(columnName));
    }

    // 其他方法实现...

    private String toJson(List<String> list) throws SQLException {
        try {
            return MAPPER.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            throw new SQLException("Error converting list to json", e);
        }
    }

    private List<String> fromJson(String json) throws SQLException {
        if (json == null) {
            return null;
        }
        try {
            return MAPPER.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            throw new SQLException("Error converting json to list", e);
        }
    }
}
```

注册类型处理器：

```java
// 注册自定义类型处理器
OrmConfig.registerTypeHandler(Product.class, Product::getTags, new ListTypeHandler());
```

### 拦截器

```java
@Component
public class AuditInterceptor implements CrudInterceptor {
    @Override
    public void beforeUpdate(Update<?, ?> update) {
        // 自动设置更新时间
        update.set("updateTime", LocalDateTime.now());
    }

    @Override
    public void beforeQuery(Query<?> query) {
        // 自动添加未删除条件
        query.eq("deleted", false);
    }
}
```

拦截器使用场景：

- 自动设置创建/更新时间
- 实现软删除功能
- 数据权限控制
- 操作审计
- 性能监控

## 配置选项

```java
// 基础配置
OrmConfig.config()
    .defaultIdType(IdType.AUTO)                        // 设置默认使用自增ID
    .tableNameStrategy(NameStrategy.LOWER_UNDERLINE)   // 表名转换策略
    .columnNameStrategy(NameStrategy.LOWER_UNDERLINE)  // 列名转换策略
    .defaultBatchSize(100)                            // 批量操作大小
    .iEnumValueEnabled(true);                         // 启用枚举值处理

// 注册ID生成器
OrmConfig.registerIdGenerator(Product.class, (entity, column) -> {
    // 自定义ID生成逻辑，例如：生成带前缀的ID
    return "P" + System.currentTimeMillis();
});

// 注册拦截器
OrmConfig.registerInterceptor(new AuditInterceptor());
```

## 最佳实践

1. 使用 `@Table` 和 `@Column` 注解显式指定表名和列名
2. 为实体类的主键添加 `@Id` 注解并指定生成策略
3. 使用类型安全的方法引用而不是字符串来指定字段
4. 适当使用拦截器来处理通用逻辑
5. 合理配置批量操作大小以优化性能
6. 查询允许空值条件，更新和删除禁止空值条件
7. 谨慎使用 `force()` 方法进行无条件操作

## 注意事项

1. 框架仅支持单表操作，不支持关联查询
2. 枚举类型需要实现 `IEnum` 接口
3. JSON 字段需要配置相应的类型处理器
4. 批量操作时注意内存占用
5. 使用 `nullable` 和 `force` 参数时需要注意安全性

## 许可证

本项目采用 [Apache License 2.0](LICENSE) 开源协议。

