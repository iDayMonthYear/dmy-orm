# MyBatis XML 中使用链式查询条件示例

本文档展示如何在 MyBatis XML 中使用 DMY-ORM 的链式查询条件，实现复杂的 SQL 查询。

## 基本用法

在 MyBatis XML 中，可以使用 `${cond}`、`${orderBy}` 和 `${groupBy}` 来引用链式查询条件。

### 使用 XmlQuery

DMY-ORM 提供了专门用于 MyBatis XML 的 `XmlQuery` 类，它会自动生成条件字符串、排序字符串和分组字符串，可以直接在 XML 中使用。

```java
// 创建 XmlQuery 对象
XmlQuery<User, Long> query = userDao.xq()
    .eq(User::getStatus, 1)
    .like(User::getName, "%张%")
    .orderByDesc(User::getCreatedAt);

// 查看生成的条件字符串
System.out.println(query.getCond());     // 输出: `status` = ? and `name` like ?
System.out.println(query.getOrderBy());  // 输出: `created_at` desc
System.out.println(query.getGroupBy());  // 输出: null (没有分组)
```

### 示例 Mapper 接口

```java
@Mapper
public interface UserMapper {
    /**
     * 多表联合查询示例
     * @param query 查询条件
     * @return 用户列表
     */
    List<UserDTO> findUserWithDept(XmlQuery<User, Long> query);
    
    /**
     * 统计查询示例
     * @param query 查询条件
     * @return 统计结果
     */
    List<UserStatDTO> countUserByDept(XmlQuery<User, Long> query);
    
    /**
     * 分页查询示例
     * @param page 分页参数
     * @param query 查询条件
     * @return 分页结果
     */
    Page<UserDTO> pageUserWithDept(Page<?> page, XmlQuery<User, Long> query);
}
```

### 示例 XML 配置

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.mapper.UserMapper">

    <!-- 多表联合查询示例 -->
    <select id="findUserWithDept" resultType="com.example.dto.UserDTO">
        SELECT u.*, d.name as deptName
        FROM user u
        LEFT JOIN department d ON u.dept_id = d.id
        <where>
            <if test="cond != null">
                ${cond}
            </if>
        </where>
        <if test="groupBy != null">
            GROUP BY ${groupBy}
        </if>
        <if test="orderBy != null">
            ORDER BY ${orderBy}
        </if>
        <if test="limit != null">
            LIMIT #{limit}
            <if test="offset != null">
                OFFSET #{offset}
            </if>
        </if>
    </select>
    
    <!-- 统计查询示例 -->
    <select id="countUserByDept" resultType="com.example.dto.UserStatDTO">
        SELECT d.name as deptName, COUNT(u.id) as userCount
        FROM user u
        LEFT JOIN department d ON u.dept_id = d.id
        <where>
            <if test="cond != null">
                ${cond}
            </if>
        </where>
        <if test="groupBy != null">
            GROUP BY ${groupBy}
        </if>
        <if test="orderBy != null">
            ORDER BY ${orderBy}
        </if>
    </select>
    
    <!-- 分页查询示例 -->
    <select id="pageUserWithDept" resultType="com.example.dto.UserDTO">
        SELECT u.*, d.name as deptName
        FROM user u
        LEFT JOIN department d ON u.dept_id = d.id
        <where>
            <if test="query.cond != null">
                ${query.cond}
            </if>
        </where>
        <if test="query.groupBy != null">
            GROUP BY ${query.groupBy}
        </if>
        <if test="query.orderBy != null">
            ORDER BY ${query.orderBy}
        </if>
        <if test="page.pageSize != null">
            LIMIT #{page.pageSize}
            <if test="page.offset != null">
                OFFSET #{page.offset}
            </if>
        </if>
    </select>
</mapper>
```

### 示例使用代码

```java
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private OrmDao<User, Long> userDao;
    
    /**
     * 查询指定部门的用户列表
     */
    public List<UserDTO> findUsersByDept(Long deptId, String keyword) {
        // 创建查询条件
        XmlQuery<User, Long> query = userDao.xq()
            .eq(User::getDeptId, deptId)
            .like(StrUtil.isNotBlank(keyword), User::getName, keyword)
            .orderByDesc(User::getCreatedAt);
        
        // 使用 MyBatis XML 执行多表查询
        return userMapper.findUserWithDept(query);
    }
    
    /**
     * 按部门统计用户数量
     */
    public List<UserStatDTO> countUsersByDept() {
        // 创建查询条件
        XmlQuery<User, Long> query = userDao.xq()
            .eq(User::getStatus, 1)  // 只统计有效用户
            .groupBy(User::getDeptId)
            .orderByDesc(SqlFn.count());
        
        // 使用 MyBatis XML 执行统计查询
        return userMapper.countUserByDept(query);
    }
    
    /**
     * 分页查询用户列表
     */
    public Page<UserDTO> pageUsersByDept(Long deptId, String keyword, int pageNo, int pageSize) {
        // 创建分页参数
        Page<Void> page = Page.of(pageNo, pageSize);
        
        // 创建查询条件
        XmlQuery<User, Long> query = userDao.xq()
            .eq(User::getDeptId, deptId)
            .like(StrUtil.isNotBlank(keyword), User::getName, keyword)
            .orderByDesc(User::getCreatedAt);
        
        // 使用 MyBatis XML 执行分页查询
        return userMapper.pageUserWithDept(page, query);
    }
    
    /**
     * 使用OrmDao内置的xmlList方法
     */
    public List<User> xmlList(Long deptId, String keyword) {
        // 创建查询条件
        XmlQuery<User, Long> query = userDao.xq()
            .eq(User::getDeptId, deptId)
            .like(StrUtil.isNotBlank(keyword), User::getName, keyword)
            .orderByDesc(User::getCreatedAt);
        
        // 使用OrmDao内置的xmlList方法
        return userDao.xmlList(query);
    }
    
    /**
     * 使用OrmDao内置的xmlPage方法
     */
    public Page<User> xmlPage(Long deptId, String keyword, int pageNo, int pageSize) {
        // 创建分页参数
        Page<Void> page = Page.of(pageNo, pageSize);
        
        // 创建查询条件
        XmlQuery<User, Long> query = userDao.xq()
            .eq(User::getDeptId, deptId)
            .like(StrUtil.isNotBlank(keyword), User::getName, keyword)
            .orderByDesc(User::getCreatedAt);
        
        // 使用OrmDao内置的xmlPage方法
        return userDao.xmlPage(page, query);
    }
}
```

## 注意事项

1. 在 XML 中使用 `${cond}` 时，需要注意 SQL 注入的风险。DMY-ORM 已经对字段名和操作符进行了安全处理，但参数值仍然通过 `?`
   占位符传递，因此是安全的。

2. 链式查询条件生成的 SQL 片段不包含 `WHERE`、`GROUP BY` 和 `ORDER BY` 关键字，需要在 XML 中自行添加。

3. 在多表查询中，如果存在字段名冲突，可以在链式查询中使用表别名前缀，例如：
   ```java
   XmlQuery<User, Long> query = userDao.xq()
       .eq("u.status", 1)  // 使用表别名前缀
       .orderBy("d.name", false);  // 使用表别名前缀
   ```

4. 对于复杂的查询逻辑，可以结合 MyBatis 的动态 SQL 和 DMY-ORM 的链式查询条件，实现更灵活的查询。

5. `XmlQuery` 类使用 `XmlQueryGenerator` 一次性生成所有查询字符串，避免多次遍历查询条件，提高性能。它采用懒加载方式，只有在实际需要时（调用
   `getCond()`、`getOrderBy()`或`getGroupBy()`方法）才会生成，并且会缓存结果以进一步提高性能。 