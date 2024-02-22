package cn.com.idmy.orm.core.query;

import cn.com.idmy.orm.core.util.CollectionUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author michael
 */
public class WithBuilder<Wrapper extends QueryWrapper> {

    private Wrapper queryWrapper;
    private With with;

    /**
     * withItem
     */
    private String name;
    private List<String> params;

    public WithBuilder() {
    }

    public WithBuilder(Wrapper queryWrapper, With with, String name) {
        this.queryWrapper = queryWrapper;
        this.with = with;
        this.name = name;
    }

    public WithBuilder(Wrapper queryWrapper, With with, String name, List<String> params) {
        this.queryWrapper = queryWrapper;
        this.with = with;
        this.name = name;
        this.params = params;
    }


    public Wrapper asSelect(QueryWrapper newWrapper) {
        WithItem withItem = new WithItem(name, params);
        withItem.setWithDetail(new WithSelectDetail(newWrapper));
        with.addWithItem(withItem);
        return queryWrapper;
    }


    public Wrapper asValues(Object[] values, QueryWrapper newWrapper) {
        WithItem withItem = new WithItem(name, params);
        withItem.setWithDetail(new WithValuesDetail(Arrays.asList(values), newWrapper));
        with.addWithItem(withItem);
        return queryWrapper;
    }


    public Wrapper asValues(Collection values, QueryWrapper newWrapper) {
        WithItem withItem = new WithItem(name, params);
        withItem.setWithDetail(new WithValuesDetail(CollectionUtil.toList(values), newWrapper));
        with.addWithItem(withItem);
        return queryWrapper;
    }

    public Wrapper asRaw(String rawSQL, Object... params) {
        WithItem withItem = new WithItem(name, this.params);
        withItem.setWithDetail(new WithStringDetail(rawSQL, params));
        with.addWithItem(withItem);
        return queryWrapper;
    }


}
