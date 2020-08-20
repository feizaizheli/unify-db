package cn.com.nwdc.db.index.common.spring.proxy;

import cn.com.nwdc.db.index.common.spring.mapper.db.IndexMapperMethod;
import org.springframework.beans.factory.BeanFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author coffee
 * @Classname EsMapperProxyFactory
 * @Description TODO
 * @Date 2020/7/18 18:56
 */
public class IndexMapperProxyFactory<T> {

    private final Class<T> mapperInterface;
    private final Map<Method, IndexMapperMethod> methodCache = new ConcurrentHashMap<Method, IndexMapperMethod>();
    private final BeanFactory beanFactory;

    public IndexMapperProxyFactory(Class<T> mapperInterface, BeanFactory beanFactory) {
        this.mapperInterface = mapperInterface;
        this.beanFactory = beanFactory;
    }

    public Class<T> getMapperInterface() {
        return mapperInterface;
    }

    public Map<Method, IndexMapperMethod> getMethodCache() {
        return methodCache;
    }

    @SuppressWarnings("unchecked")
    public T newInstance(IndexMapperProxy<T> mapperProxy) {
        /*if(mapperProxy == null){
            mapperProxy = new EsMapperProxy<>(mapperInterface,beanFactory);
        }*/
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[] { mapperInterface }, mapperProxy);
    }


}
