package cn.com.nwdc.db.index.common.spring.proxy;

import cn.com.nwdc.db.index.common.spring.mapper.db.IndexMapperMethod;
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

        cn.com.nwdc.db.index.common.spring.mapper.IndexMapperMethod esMapperMethod = new IndexMapperMethod(proxy,
                beanFactory,
                mapperInterface,
                new cn.com.nwdc.db.index.common.spring.mapper.IndexMapperMethod.MethodSignature(proxy,method,args));
        return esMapperMethod.execute();

    }
}
