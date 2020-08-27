package cn.com.nwdc.unify.db.index.common.spring.proxy;

import cn.com.nwdc.unify.db.index.common.spring.mapper.IndexMapperMethod;
import cn.com.nwdc.unify.db.index.common.spring.mapper.db.DefaultIndexMapperMethod;
import org.springframework.beans.factory.BeanFactory;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author coffee
 * @Classname EsMapperProxy
 * @Description TODO
 * @Date 2020/7/18 18:54
 */
public class IndexMapperProxy<T> implements InvocationHandler, Serializable {


    private final Class<T> mapperInterface;

    private final BeanFactory beanFactory;

    public IndexMapperProxy(Class<T> mapperInterface, BeanFactory beanFactory) {
        this.mapperInterface = mapperInterface;
        this.beanFactory = beanFactory;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        IndexMapperMethod esMapperMethod = new DefaultIndexMapperMethod(proxy,
                beanFactory,
                mapperInterface,
                new IndexMapperMethod.MethodSignature(proxy,method,args));
        return esMapperMethod.execute();

    }
}
