package cn.com.nwdc.db.index.common.method.strategy;

import cn.com.nwdc.db.index.common.IndexDbCondition;
import cn.com.nwdc.db.index.common.spring.mapper.IndexMapperMethod;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author coffee
 * @Classname ESMethodTypeStrategyList
 * @Description TODO
 * @Date 2020/7/17 22:12
 */

@Component("esMethodTypeStrategyList")
public class IndexMethodTypeStrategyList extends AbstractIndexMethodTypeStrategy<List> {


    @Override
    public boolean isMatch(Class returnType) {
        return List.class == returnType;
    }

    @Override
    public List executeUpdate(Object proxy, String templateName, IndexMapperMethod.MethodSignature methodSignature) {


        return executeUpdate(null,proxy,templateName,methodSignature);


    }


    public List executeUpdate(Class<?> elemType,Object proxy, String templateName, IndexMapperMethod.MethodSignature methodSignature) {


        if(elemType == null){
            elemType = getElemTypes(methodSignature.getMethod().getGenericReturnType());
        }

        IndexDbCondition esDbCondition = IndexDbCondition.builder().build().template(
                templateName,methodSignature.getMethod().getName()
        );
        String tableName = methodSignature.getTableName();
        if(StringUtils.isBlank(tableName)){
            throw new IllegalArgumentException("please set tableName ,["+methodSignature.getMethod()+"] no @ParamTableName or @ParamTableNameStrategyBean");
        }
        esDbCondition.setTableName(tableName);
        esDbCondition.setParams(methodSignature.getParams());
        esDbCondition.setRootObject(methodSignature.getRootObject());

        if(elemType == Map.class|| HashMap.class == elemType || LinkedHashMap.class == elemType){
            if(StringUtils.isBlank(tableName)){
                throw new IllegalArgumentException("please set @ParamTableName use List<Map>");
            }

            return dbOperator.queryForMapList(esDbCondition);
        }else{
            return dbOperator.queryForList(esDbCondition);

        }


    }
}
