

package cn.com.idmy.orm.spring.datasource;

import cn.com.idmy.orm.annotation.UseDataSource;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.aop.support.annotation.AnnotationMethodMatcher;

/**
 * 多数据源切面。
 *
 * @author 王帅
 * @since 2023-06-25
 */
public class DataSourceAdvice extends AbstractPointcutAdvisor {
    private final Advice advice;
    private final Pointcut pointcut;

    public DataSourceAdvice() {
        advice = new DataSourceInterceptor();
        var cpc = new AnnotationMatchingPointcut(UseDataSource.class, true);
        var mpc = new AnnotationMethodMatcher(UseDataSource.class);
        pointcut = new ComposablePointcut(mpc).union(cpc);
    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }


    @Override
    public Advice getAdvice() {
        return this.advice;
    }

}
