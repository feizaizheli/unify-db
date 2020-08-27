package cn.com.nwdc.unify.db.index.common.method.strategy;

import cn.com.nwdc.unify.db.index.common.IndexException;
import cn.com.nwdc.unify.db.index.common.IndexDbOperator;
import cn.com.nwdc.unify.db.index.common.method.IndexMethodTypeStrategy;
import cn.com.nwdc.unify.db.index.common.spring.IndexSessionFactoryBean;
import cn.com.nwdc.unify.db.index.common.spring.mapper.IndexMapperMethod;
import cn.com.nwdc.unify.utils.ClassUtil;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author coffee
 * @Classname AbstractESMethodTypeStrategy
 * @Description TODO
 * @Date 2020/7/17 22:14
 */
public abstract class AbstractIndexMethodTypeStrategy<TYPE> implements IndexMethodTypeStrategy<TYPE> {
    @Autowired
    @Qualifier("indexDbOperator")
    protected IndexDbOperator indexDbOperator;

    @Override
    public TYPE execute(Object proxy,String templateName, IndexMapperMethod.MethodSignature methodSignature) {

        return executeUpdate( proxy,templateName,methodSignature);

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
