package cn.com.nwdc.db.index.common.method.strategy;

import cn.com.nwdc.db.condition.DbCondition;
import cn.com.nwdc.db.index.common.IndexDbCondition;
import cn.com.nwdc.db.index.common.spring.mapper.IndexMapperMethod;
import cn.com.nwdc.db.pageinfo.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author coffee
 * @Classname ESMethodTypeStrategyPageInfo
 * @Description TODO
 * @Date 2020/7/17 22:12
 */

@Component
public class IndexMethodTypeStrategyPageInfo extends AbstractIndexMethodTypeStrategy<PageInfo> {


    private static final Logger LOGGER = LoggerFactory.getLogger(IndexMethodTypeStrategyPageInfo.class);

    @Override
    public boolean isMatch(Class<?> returnType) {
        return PageInfo.class == returnType;
    }

    @Override
    public PageInfo executeUpdate(Object proxy, String templateName, IndexMapperMethod.MethodSignature methodSignature ) {
        DbCondition esDbCondition = IndexDbCondition.builder().build().template(
                templateName,methodSignature.getMethod().getName()
        );
        Class<?> elemType = getElemTypes(methodSignature.getMethod().getGenericReturnType());
        esDbCondition.setElemClass(elemType);
        String tableName = methodSignature.getTableName();
        esDbCondition.setTableName(tableName);
        esDbCondition.setParams(methodSignature.getParams());
        esDbCondition.setRootObject(methodSignature.getRootObject());
        return dbOperator.queryForPage(esDbCondition,new PageInfo());



    }




}
