package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.paginate.Page;

import java.util.List;
import java.util.Optional;

/**
 * <p>链式查询接口。
 *
 * <p>该接口定义了通用的链式查询方法：
 * <ul>
 *     <li><b>one</b>: 查询一条数据。
 *     <li><b>list</b>: 查询多条数据。
 *     <li><b>page</b>: 分页查询数据。
 * </ul>
 *
 * @param <T> 实体类类型
 * @author 王帅
 * @since 2023-08-08
 */
public interface ChainQuery<T> {

    /**
     * 获取一条数据。
     *
     * @return 一条数据
     */
    T one();

    /**
     * 获取一条数据，返回的数据为 asType 类型。
     *
     * @param asType 接收数据类型
     * @param <R>    接收数据类型
     * @return 一条数据
     */
    <R> R oneAs(Class<R> asType);

    /**
     * 获取一条数据，并封装为 {@link Optional} 返回。
     *
     * @return 一条数据
     */
    default Optional<T> oneOpt() {
        return Optional.ofNullable(one());
    }

    /**
     * 获取一条数据，返回的数据为 asType 类型，并封装为 {@link Optional} 返回。
     *
     * @param asType 接收数据类型
     * @param <R>    接收数据类型
     * @return 一条数据
     */
    default <R> Optional<R> oneAsOpt(Class<R> asType) {
        return Optional.ofNullable(oneAs(asType));
    }

    /**
     * 获取多条数据。
     *
     * @return 数据列表
     */
    List<T> list();

    /**
     * 获取多条数据，返回的数据为 asType 类型。
     *
     * @param asType 接收数据类型
     * @param <R>    接收数据类型
     * @return 数据列表
     */
    <R> List<R> listAs(Class<R> asType);

    /**
     * 获取分页数据。
     *
     * @param page 分页对象
     * @return 分页数据
     */
    Page<T> page(Page<T> page);

    /**
     * 获取分页数据，返回的数据为 asType 类型。
     *
     * @param page   分页对象
     * @param asType 接收数据类型
     * @param <R>    接收数据类型
     * @return 分页数据
     */
    <R> Page<R> pageAs(Page<R> page, Class<R> asType);

}
