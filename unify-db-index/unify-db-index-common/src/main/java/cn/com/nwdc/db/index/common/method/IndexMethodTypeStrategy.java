package cn.com.nwdc.db.index.common.method;


import cn.com.nwdc.db.index.common.spring.mapper.IndexMapperMethod;

/**
 * @author coffee
 * @Classname ESDbTypeAdapter
 * @Description TODO
 * @Date 2020/7/17 22:09
 */
public interface IndexMethodTypeStrategy<TYPE> {


    boolean isMatch(Class<?> returnType);


    TYPE execute(Object proxy, String elasticSearchMapper, IndexMapperMethod.MethodSignature methodSignature);


}
