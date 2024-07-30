
package cn.com.idmy.orm.spring.boot;


import cn.com.idmy.orm.core.OrmGlobalConfig;

/**
 * <p>MyBatis-Flex 配置。
 *
 * <p>一般可以用于去初始化：
 *
 * <ul>
 *      <li>FlexGlobalConfig 的全局配置
 *      <li>自定义主键生成器
 *      <li>多租户配置
 *      <li>动态表名配置
 *      <li>逻辑删除处理器配置
 *      <li>自定义脱敏规则
 *      <li>SQL 审计配置
 *      <li>SQL 打印配置
 *      <li>数据源解密器配置
 *      <li>自定义数据方言配置
 *      <li>...
 * </ul>
 */
public interface OrmCustomizer {

    /**
     * 自定义 MyBatis-Flex 配置。
     *
     * @param globalConfig 全局配置
     */
    void customize(OrmGlobalConfig globalConfig);
}
