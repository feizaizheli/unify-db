package cn.com.nwdc.db.index.common.spring.mapper.db;

import cn.com.nwdc.db.index.common.method.IndexMethodTypeStrategyFactory;
import cn.com.nwdc.db.index.common.method.IndexMethodTypeStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;

/**
 * @author coffee
 * @Classname DbEsMapperMethod
 * @Description TODO
 * @Date 2020/7/20 9:00
 */
public class IndexMapperMethod implements cn.com.nwdc.db.index.common.spring.mapper.IndexMapperMethod {

    private static final Logger LOGGER = LoggerFactory.getLogger(cn.com.nwdc.db.index.common.spring.mapper.IndexMapperMethod.class);

    private cn.com.nwdc.db.index.common.spring.mapper.IndexMapperMethod.MethodSignature methodSignature;

    private Class<?> mapperInterface;

    private Object proxy;

    private IndexMethodTypeStrategyFactory methodTypeStrategyFactory;

    public IndexMapperMethod(Object proxy, BeanFactory beanFactory, Class<?> mapperInterface, MethodSignature method) {
        this.proxy = proxy;
        this.mapperInterface = mapperInterface;
        this.methodSignature = method;
        this.methodTypeStrategyFactory = beanFactory.getBean(IndexMethodTypeStrategyFactory.class);

    }

    @Override
    public Object execute(){


        IndexMethodTypeStrategy methodTypeStrategy = methodTypeStrategyFactory.selectStrategy(proxy,methodSignature.getMethod(),methodSignature.getReturnType());
        return methodTypeStrategy.execute(proxy,
                getTemplateName(mapperInterface.getSimpleName()),methodSignature);
    }


    protected String getTemplateName(String mapperName){

     /*   if(mapperName.endsWith(MAPPER)){
            return mapperName.substring(0,mapperName.lastIndexOf(MAPPER));
        }*/
        return mapperName;
    }



}
