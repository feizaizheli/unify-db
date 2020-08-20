package cn.com.nwdc.db.rdb.common.utils;

import cn.com.nwdc.db.anno.cache.TableInfoHelper;
import cn.com.nwdc.db.anno.cache.info.TableInfo;
import cn.com.nwdc.db.rdb.common.type.TypeAdapterResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author heff
 * @Classname MapToObjectUtils
 * @Description TODO
 * @Date 2020/3/23 9:00
 * @group smart video north
 */
public class BeanUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(BeanUtils.class);


    public static <ELEM> ELEM mapToObject(Map<String, Object> objectMap, Class<?> elemClass) {

        return mapToObject(objectMap,elemClass,false);
    }


        //把map组装成对应的实体类返回
    public static <ELEM> ELEM mapToObject(Map<String, Object> objectMap, Class<?> elemClass,boolean isTypeAdapter) {
        Field beanField ;
        String colName=null,fieldName=null;
        Object colVal = null;
        try {
            TableInfo tableInfo = TableInfoHelper.getTableInfo(elemClass);
            ELEM object = (ELEM) elemClass.newInstance();
            for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
                colName = entry.getKey();
                colVal = entry.getValue();
                if (colVal != null) {
                    fieldName = tableInfo.mappingFieldByColumn(colName);
                    beanField = elemClass.getDeclaredField(fieldName);
                    beanField.setAccessible(true);
                    if(isTypeAdapter){
                        colVal = TypeAdapterResolver.getValue(colVal.getClass(),beanField.getType(),colVal);
                    }
                    ReflectionUtils.setField(beanField, object, colVal);
                }
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("[Field:{}][col:{}] assignment exception！",fieldName,colName, e);
            throw new RuntimeException("[Field:"+fieldName+"][col:"+colName+"] assignment exception！");
        }
    }





}
