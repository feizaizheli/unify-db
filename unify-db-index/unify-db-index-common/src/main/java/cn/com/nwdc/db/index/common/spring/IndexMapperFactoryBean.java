package cn.com.nwdc.db.index.common.spring;

import cn.com.nwdc.db.index.common.spring.proxy.IndexMapperProxy;
import cn.com.nwdc.db.index.common.spring.proxy.IndexMapperProxyFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author coffee
 * @Classname EsMapperFactoryBean
 * @Description TODO
 * @Date 2020/7/18 17:58
 */
public class IndexMapperFactoryBean<T> implements FactoryBean<T> {

    private Class<T> mapperInterface;

    private BeanFactory beanFactory;

    private IndexMapperProxyFactory<T> esMapperProxyFactory;



    public IndexMapperFactoryBean() {

    }

    public IndexMapperFactoryBean(Class<T> mapperInterface, BeanFactory beanFactory) {
        this.mapperInterface = mapperInterface;
        this.beanFactory = beanFactory;
        esMapperProxyFactory = new IndexMapperProxyFactory<>(mapperInterface,beanFactory);
    }

    @Override
    public T getObject() throws Exception {


        IndexMapperProxy mapperProxy = new IndexMapperProxy<>(mapperInterface,beanFactory);
        return (T)esMapperProxyFactory.newInstance(mapperProxy);

    }

    @Override
    public Class<?> getObjectType() {
        return mapperInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
