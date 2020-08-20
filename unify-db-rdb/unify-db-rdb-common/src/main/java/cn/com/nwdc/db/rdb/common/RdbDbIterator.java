package cn.com.nwdc.db.rdb.common;


import cn.com.nwdc.db.anno.cache.TableInfoHelper;
import cn.com.nwdc.db.anno.cache.info.TableFieldInfo;
import cn.com.nwdc.db.anno.cache.info.TableInfo;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author coffee
 * @Classname RdbDbIterator
 * @Description TODO
 * @Date 2019/9/10 16:18
 */
public class RdbDbIterator<ELEM> implements Iterator {

    private static final Logger LOGGER = LoggerFactory.getLogger(RdbDbIterator.class);

    private ResultSet resultSet;

    private String idName;

    private Set<String> colMetaData;
    ;

    private Class<?> elemClass;

    public RdbDbIterator(ResultSet resultSet, Class<?> elemClass) {
        this.resultSet = resultSet;
        this.elemClass = elemClass;
        if (colMetaData == null) {
            colMetaData = Sets.newHashSet();
        }
        try {
            for (int i = 1; i <= this.resultSet.getMetaData().getColumnCount(); i++) {
                String colName = resultSet.getMetaData().getColumnName(i);
                colMetaData.add(colName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.error("ResultSet metadatag build exception！", e);
            throw new IllegalArgumentException("ResultSet metadatag build exception！");
        }


    }

    @Override
    public boolean hasNext() {
        try {
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public ELEM next() {
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            for (String colName : colMetaData) {
                map.put(colName, resultSet.getObject(colName));
            }
            return (ELEM)map;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //把map组装成对应的实体类返回
    protected ELEM mapToObject(Map<String, Object> objectMap) {
        try {
            ELEM object = (ELEM) elemClass.newInstance();
            TableInfo tableInfo = TableInfoHelper.getTableInfo(elemClass);
            for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
                setFieldValue(tableInfo, object, entry);
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("map Convert to object list exception");
        }
    }
    //给对象的字段赋值
    protected void setFieldValue(TableInfo tableInfo,ELEM object,Map.Entry<String, Object> entry) {
        try {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();
            if(fieldValue!=null) {
                if (tableInfo.getKeyColumn().toUpperCase().equals(fieldName.toUpperCase())) {
                    fieldName = tableInfo.getKeyProperty();
                } else {
                    for (TableFieldInfo fieldInfo : tableInfo.getFieldList()) {
                        if (fieldInfo.getColumn().toUpperCase().equals(fieldName.toUpperCase())) {
                            fieldName = fieldInfo.getProperty();
                            break;
                        }
                    }
                }
                Field field = elemClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                //ReflectionUtils.setField(field, object, ConvertUtils.convert(String.valueOf(fieldValue), field.getType()));
                ReflectionUtils.setField(field, object, fieldValue);

            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            LOGGER.error("Field assignment exception！", e);
            throw new RuntimeException("Field assignment exception");
        }
    }
}
