package cn.com.nwdc.db.index.common.method;

import cn.com.nwdc.db.dl.IDbDML;
import cn.com.nwdc.db.index.common.IndexDbOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author coffee
 * @Classname ESMethodTypeStrategyFactory
 * @Description TODO
 * @Date 2020/7/18 14:40
 */

@Component
public class IndexMethodTypeStrategyFactory {


    private static final Logger LOGGER = LoggerFactory.getLogger(IndexMethodTypeStrategyFactory.class);
    @Autowired
    private Set<IndexMethodTypeStrategy> esMethodTypeStrategys;

    @Autowired
    @Qualifier("ESMethodTypeStrategyObject")
    private IndexMethodTypeStrategy defaultMethodTypeStrategy;


    public IndexMethodTypeStrategy selectStrategy(Object proxy, Method method, Class<?> returnType) {
        if(method.getDeclaringClass()!= IndexDbOperator.class && method.getDeclaringClass()!= IDbDML.class){
            for (IndexMethodTypeStrategy esMethodTypeStrategy : esMethodTypeStrategys) {
                if (esMethodTypeStrategy.isMatch(returnType)) {
                    return esMethodTypeStrategy;
                }
            }
        }

        return defaultMethodTypeStrategy;

    }


}
