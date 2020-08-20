package cn.com.nwdc.db.index.common.method.strategy;

import cn.com.nwdc.db.index.common.spring.mapper.IndexMapperMethod;
import cn.com.nwdc.utils.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author coffee
 * @Classname ESMethodTypeStrategyRaw
 * @Description TODO
 * @Date 2020/7/18 15:00
 */

@Component
public class IndexMethodTypeStrategyObject extends AbstractIndexMethodTypeStrategy<Object> {



    @Autowired
    @Qualifier("esMethodTypeStrategyList")
    private IndexMethodTypeStrategyList esMethodTypeStrategyList;

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexMethodTypeStrategyObject.class);




    @Override
    protected Object executeUpdate(Object proxy, String templateName, IndexMapperMethod.MethodSignature methodSignature) {



        Method method = methodSignature.getMethod();

        if (methodSignature.getRootObject()!=null) {

            List dataList = esMethodTypeStrategyList.executeUpdate(method.getReturnType(),proxy,templateName,methodSignature);
            if(dataList!=null && dataList.size()>0){
                return dataList.get(0);
            }
        }
        Method esMethod = ClassUtil.getDeclaredMethod(dbOperator,method.getName(),method.getParameterTypes());

        if(esMethod!=null){
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("EsDbOperator[] execute...",esMethod.getName());
            }

            return ClassUtil.invokeMethod(dbOperator,method.getName(),method.getParameterTypes(),methodSignature.getArgVals());

        }else{
            //toDo excute default method
            return null;
        }

     //   dbOperator.executeRestRequest()

    }

    @Override
    public boolean isMatch(Class<?> returnType) {
        return false;
    }
}
