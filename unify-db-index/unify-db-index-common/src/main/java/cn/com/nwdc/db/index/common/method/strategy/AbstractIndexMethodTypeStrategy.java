package cn.com.nwdc.db.index.common.method.strategy;

import cn.com.nwdc.db.index.common.IndexException;
import cn.com.nwdc.db.index.common.IndexDbOperator;
import cn.com.nwdc.db.index.common.method.IndexMethodTypeStrategy;
import cn.com.nwdc.db.index.common.spring.mapper.IndexMapperMethod;
import cn.com.nwdc.utils.ClassUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.reflect.Type;

/**
 * @author coffee
 * @Classname AbstractESMethodTypeStrategy
 * @Description TODO
 * @Date 2020/7/17 22:14
 */
public abstract class AbstractIndexMethodTypeStrategy<TYPE> implements IndexMethodTypeStrategy<TYPE> {
    @Autowired
    @Qualifier("esDbOperator")
    protected IndexDbOperator dbOperator;


    @Override
    public TYPE execute(Object proxy,String templateName, IndexMapperMethod.MethodSignature methodSignature) {


        return executeUpdate( proxy,
                templateName,
                methodSignature);

    }

    protected abstract TYPE executeUpdate(Object proxy,String templateName,  IndexMapperMethod.MethodSignature methodSignature);
    protected Class<?> getElemTypes(Type returnType) {
        Class<?>[] elemTypes = new Class[0];
        try {
            elemTypes = ClassUtil.getTypeGenericity(returnType);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IndexException("please set pageInfo T");
        }

        if(elemTypes == null || elemTypes.length<1){
            throw new IndexException("please set pageInfo T");
        }
        return elemTypes[0];
    }


}
