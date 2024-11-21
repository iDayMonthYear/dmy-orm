# DMY-ORM

DMY-ORM 是一个轻量级的 ORM 框架，基于 MyBatis 构建，提供类型安全的链式查询、灵活的类型处理和强大的枚举支持。

## 特性

- 类型安全的链式查询 API
- 自动类型转换
- JSON 字段处理
- 自定义 ID 生成策略
- Spring Boot 集成
- 数据库模式验证
- 枚举值处理

## 快速开始

### Maven 依赖

```xml
<dependency>
    <groupId>cn.com.idmy</groupId>
    <artifactId>dmy-orm-spring-boot-starter</artifactId>
    <version>${version}</version>
</dependency>
```

### 实体定义

```java
@Table("t_user")
public class User {
    @Table.Id(type = Type.AUTO)
    private Long id;
    
    @Table.Column("user_name") 
    private String name;
    
    @EnumValue
    private UserStatus status;
    
    @Column
    private JSONObject extra;
}
```

### DAO 接口

```java
public interface UserDao extends MybatisDao<User, Long> {
    // 所有基本的 CRUD 操作均继承
}
```

### 基本用法

```java
// 查询
List<User> users = userDao.find(
    SelectChain.of(userDao)
        .select(User::getName, User::getStatus)
        .eq(User::getStatus, UserStatus.ACTIVE)
);

// 获取单条记录
User user = userDao.get(1L);

// 更新
int rows = userDao.update(
   UpdateChain.of(userDao)
    .set(User::getStatus, UserStatus.INACTIVE)
    .eq(User::getId, 1)
);

// 删除
userDao.delete(DeleteChain.of(userDao).eq(User::getStatus, UserStatus.INACTIVE));
```

## 核心功能

#### 单条记录查询

```java
// 根据 ID 查询
User user = userDao.get(1L);

// 条件查询单条记录
User user = userDao.get(
    SelectChain.of(userDao)
        .eq(User::getStatus, UserStatus.ACTIVE)
);

// 查询单个字段
String name = userDao.get(User::getName, 1L);

// 条件查询指定字段
Integer age = userDao.get(
    User::getAge,
    SelectChain.of(userDao)
        .eq(User::getStatus, UserStatus.ACTIVE)
);
```

#### 列表查询

```java
// 查询所有记录
List<User> users = userDao.all();

// 根据 ID 集合查询
List<User> users = userDao.find(Arrays.asList(1L, 2L, 3L));

// 条件查询
List<User> users = userDao.find(
    SelectChain.of(userDao)
        .select(User::getName, User::getAge)
        .eq(User::getStatus, UserStatus.ACTIVE)
        .gt(User::getAge, 18)
        .orderBy(User::getCreateTime, true)
);

// 查询指定字段列表
List<String> names = userDao.find(
    User::getName,
    SelectChain.of(userDao)
        .eq(User::getStatus, UserStatus.ACTIVE)
);
```

#### 链式查询

```java
SelectChain.of(userDao)
.eq(User::getStatus, status) // 等于
.ne(User::getStatus, status) // 不等于
.gt(User::getAge, 18) // 大于
.ge(User::getAge, 18) // 大于等于
.lt(User::getAge, 60) // 小于
.le(User::getAge, 60) // 小于等于
.like(User::getName, "%张%") // 模糊查询
.in(User::getId, ids) // IN 查询
.isNull(User::getDeleteTime) // IS NULL
.isNotNull(User::getUpdateTime); // IS NOT NULL
```

#### Map 转换

```java
// ID 作为 key
Map<Long, User> userMap = userDao.map(1L, 2L, 3L);

// 指定字段作为 key
Map<String, User> nameMap = userDao.map(
    User::getName,
    SelectChain.of(userDao)
        .eq(User::getStatus, UserStatus.ACTIVE)
);
```

#### 存在性判断

```java
// 判断 ID 是否存在
boolean exists = userDao.exists(1L);

// 判断条件是否存在匹配记录
boolean exists = userDao.exists(
    SelectChain.of(userDao)
        .eq(User::getStatus, UserStatus.ACTIVE)
);
```

#### 排序和分组

```java
SelectChain.of(userDao)
.groupBy(User::getDeptId)
.orderBy(User::getCreateTime, true) // DESC
.orderBy(User::getId, false); // ASC
```

#### 聚合函数

```java
// COUNT
long count = userDao.count(SelectChain.of(userDao));
// SUM
BigDecimal total = userDao.sum(User::getAmount, chain);
// AVG
Double avg = userDao.avg(User::getScore, chain);
// MAX/MIN
Integer max = userDao.max(User::getAge, chain);
Integer min = userDao.min(User::getAge, chain);
```

### 枚举处理

#### 枚举定义

```java
@Getter
@RequiredArgsConstructor
public enum UserStatus implements IEnum<Integer> {
    ACTIVE(1),
    INACTIVE(0);

    @EnumValue
    private final Integer value;
    private final String name;
}
```

#### 枚举监听

```java
@Component
public class UserStatusListener {
   @WatchEnum(
      entity = User.class,
      value = UserStatus.class,
      action = Action.UPDATE,
      timing = Timing.BEFORE
   )
   public void onStatusChange(User user) {
       // 处理状态变更
   }
}
```

### 类型处理

#### JSON 类型

```java
@Table("t_config")
public class Config {
   private JSONObject settings;
   private JSONArray tags;
}
```

### ID 生成

```java
@Table("t_order")
public class Order {
    @Table.Id(type = Type.GENERATOR)
    private String id;
    // ...
}

public class OrderIdGenerator implements IdGenerator {
    @Override
    public Object generate(Object entity, String column) {
        return "ODR" + System.currentTimeMillis();
    }
}
```

## 配置说明

### application.yml

```yaml
dmy:
  orm:
    checkDatabaseColumn: true # 启用实体类对应的表结构校验
```

## 最佳实践

- 使用 Lambda 表达式指定字段，避免字符串硬编码。
- 复杂查询条件建议使用链式调用构建。
- 查询指定字段时使用 select() 方法显式指定。
- 大量数据查询时建议使用分页。
- 注意 Map 转换时 key 的唯一性。

## 注意事项

- 批量插入时主键不会回写到实体类。
- 使用枚举监听时注意避免循环依赖。
- 表结构校验会增加应用启动时间。
- 合理使用缓存提升性能。
- 聚合函数仅支持数值类型字段。
- 批量查询时注意性能问题。
- 复杂查询建议使用原生 SQL。
- 链式查询支持方法可查看 SelectChain 接口定义。
- 字段映射关系通过 @Table.Column 注解配置。

## 常见问题

### 如何使用分页查询？

```java
List<User> users = userDao.find(
    SelectChain.of(userDao)
        .limit(10)
        .offset(0)
);
```

### 如何使用动态条件？

```java
SelectChain<User> chain = SelectChain.of(userDao);
chain.eq(User::getStatus, status, status != null);
if (StringUtils.isNotBlank(name)) {
    chain.like(User::getName, "%" + name + "%");
}
```

### 如何使用 OR 条件？

```java
userDao.find(
   SelectChain.of(userDao)
      .or()
      .eq(User::getStatus, UserStatus.ACTIVE)
      .eq(User::getStatus, UserStatus.PENDING)
);
```

## 联系信息

如需支持或有任何问题，请联系 [dmy@idmy.com.cn](mailto:dmy@idmy.com.cn)。

## 许可证

[Apache License 2.0](LICENSE)