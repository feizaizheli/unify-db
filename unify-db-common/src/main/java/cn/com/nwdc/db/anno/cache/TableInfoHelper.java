package cn.com.nwdc.db.anno.cache;


import cn.com.nwdc.db.anno.TableRef;
import cn.com.nwdc.db.anno.cache.info.TableFieldInfo;
import cn.com.nwdc.db.anno.cache.info.TableInfo;
import cn.com.nwdc.utils.ClassUtil;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author heffb
 * @Classname TableInfoHelper
 * @Description TODO
 * @Date 2019/10/29 11:11
 * @group smart video north
 */
public class TableInfoHelper {

    private static final Log logger = LogFactory.getLog(com.baomidou.mybatisplus.toolkit.TableInfoHelper.class);
    /**
     * 缓存反射类表信息
     */
    private static final Map<Class<?>, TableInfo> tableInfoCache = new ConcurrentHashMap<>();
    /**
     * 默认表主键
     */
    private static final String DEFAULT_ID_NAME = "id";

   /* *//**
     * <p>
     * 获取实体映射表信息
     * <p>
     *
     * @param clazz 反射实体类
     * @return
     *//*
    public static TableInfo getTargetTableInfo(Class<?> clazz) {

        return initTableInfo(clazz);
        //return tableInfoCache.get(clazz.getName());
    }*/


    /**
     * <p>
     * 实体类反射获取表信息【初始化】
     * <p>
     *
     * @param clazz 反射实体类
     * @return
     */
    public synchronized static TableInfo getTableInfo(Class<?> clazz) {
        TableInfo ti = tableInfoCache.get(clazz);
        if (ti != null) {
            return ti;
        }
        TableInfo tableInfo = new TableInfo();
        /* 表名 */
        TableName table = clazz.getAnnotation(TableName.class);
        String tableName = clazz.getSimpleName();
        if (table != null && StringUtils.isNotEmpty(table.value())) {
            tableName = table.value();
        }
        tableInfo.setTableName(tableName);
        List<TableFieldInfo> fieldList = new ArrayList<>();
        List<Field> list = getAllFields(clazz);
        boolean existTableId = existTableId(list);
        for (Field field : list) {
            //主键Id初始化
            if (existTableId) {
                if (initTableId(tableInfo, field, clazz)) {
                    existTableId = false;
                    continue;
                }
            } else if (initFieldId(tableInfo, field, clazz)) {
                continue;
            }
            //字段初始化
            if (initTableField(tableInfo, fieldList, field, clazz)) {
                continue;
            }else if (initTableRef(fieldList, field)){
                continue;
            }

        }
        /* 字段列表 */
        tableInfo.setFieldList(fieldList);
        /*
         * 未发现主键注解，提示警告信息
         */
        if (StringUtils.isEmpty(tableInfo.getKeyColumn())) {
            logger.warn(String.format("Warn: Could not find @TableId in Class: %s.", clazz.getName()));
        }
        /*
         * 注入
         */


        /*
         * 注入
         */
        tableInfoCache.put(clazz, tableInfo);
        return tableInfo;
    }

    /**
     * <p>
     * 判断主键注解是否存在
     * </p>
     *
     * @param list 字段列表
     * @return
     */
    public static boolean existTableId(List<Field> list) {
        boolean exist = false;
        for (Field field : list) {
            TableId tableId = field.getAnnotation(TableId.class);
            if (tableId != null) {
                exist = true;
                break;
            }
        }
        return exist;
    }

    /**
     * <p>
     * 主键属性初始化
     * </p>
     *
     * @param tableInfo
     * @param field
     * @param clazz
     * @return true 继续下一个属性判断，返回 continue;
     */
    private static boolean initTableId(TableInfo tableInfo, Field field, Class<?> clazz) {
        TableId tableId = field.getAnnotation(TableId.class);
        if (tableId != null) {
            if (StringUtils.isEmpty(tableInfo.getKeyColumn())) {
                if (IdType.NONE != tableId.type()) {
                    tableInfo.setIdType(tableId.type());
                }
            }
            /* 字段 */


            String column = field.getName();
            if (StringUtils.isNotEmpty(tableId.value())) {
                column = tableId.value();
            }
            tableInfo.setKeyColumn(column);
            tableInfo.setKeyProperty(field.getName());
            return true;
        } else {
            throwExceptionId(clazz);
        }
        return false;
    }




    /**
     * <p>
     * 主键属性初始化
     * </p>
     *
     * @param tableInfo
     * @param field
     * @param clazz
     * @return true 继续下一个属性判断，返回 continue;
     */
    private static boolean initFieldId(TableInfo tableInfo, Field field, Class<?> clazz) {
        String column = field.getName();
        if (DEFAULT_ID_NAME.equalsIgnoreCase(column)) {
            if (StringUtils.isEmpty(tableInfo.getKeyColumn())) {
                tableInfo.setKeyColumn(column);
                tableInfo.setKeyProperty(field.getName());
                return true;
            } else {
                throwExceptionId(clazz);
            }
        }
        return false;
    }

    /**
     * <p>
     * 发现设置多个主键注解抛出异常
     * </p>
     */
    private static void throwExceptionId(Class<?> clazz) {
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("There must be only one, Discover multiple @TableId annotation in ");
        errorMsg.append(clazz.getName());
        throw new MybatisPlusException(errorMsg.toString());
    }

    /**
     * <p>
     * 字段属性初始化
     * </p>
     *
     * @param tableInfo 表信息
     * @param fieldList 字段列表
     * @param clazz     当前表对象类
     * @return true 继续下一个属性判断，返回 continue;
     */
    private static boolean initTableField(TableInfo tableInfo, List<TableFieldInfo> fieldList,
                                          Field field, Class<?> clazz) {
        /* 获取注解属性，自定义字段 */
        TableField tableField = field.getAnnotation(TableField.class);
        if (tableField != null) {
            String columnName = field.getName();
            if (StringUtils.isNotEmpty(tableField.value())) {
                columnName = tableField.value();
            }
            fieldList.add(new TableFieldInfo(columnName,field.getName(),field));

            return true;
        }
        return false;
    }

    /**
     * <p>
     *  ref属性初始化
     * </p>
     * @param fieldList
     * @param field
     * @return
     */
    private static boolean initTableRef(List<TableFieldInfo> fieldList,
                                          Field field) {
        /* 获取注解属性，自定义字段 */

        if (field.isAnnotationPresent(TableRef.class)) {
            TableRef tableRef = AnnotationUtils.findAnnotation(field, TableRef.class);
            field.setAccessible(true);
            TableFieldInfo tableFieldInfo;
            TableInfo tableInfo;
            TableInfo refTableInfo = null;
            //获取refClass
            if(tableRef.refClass() != null){
                refTableInfo = getTableInfo(tableRef.refClass());
            }
            //获取tableInfo
            tableInfo = getTableInfo(ClassUtil.getListGenericity(field));
            tableFieldInfo = new TableFieldInfo(null, field.getName(), field);
            tableFieldInfo.initTableRefInfo(
                    tableRef.refClass(),
                    tableRef.sourceField(),
                    tableRef.targetField(),
                    tableRef.inverse(),
                    tableRef.cascade(),
                    ClassUtil.isListType(field)||ClassUtil.isSetType(field),
                    tableInfo,
                    refTableInfo
            );
            fieldList.add(tableFieldInfo);
            return true;
        }
        return false;
    }

    /**
     * 获取该类的所有属性列表
     *
     * @param clazz 反射类
     * @return
     */
    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fieldList = ReflectionKit.getFieldList(clazz);
        if (CollectionUtils.isNotEmpty(fieldList)) {
            Iterator<Field> iterator = fieldList.iterator();
            while (iterator.hasNext()) {
                Field field = iterator.next();
                /* 过滤注解非表字段属性 */
                TableField tableField = field.getAnnotation(TableField.class);
                if (tableField != null && !tableField.exist()) {
                    iterator.remove();
                }
            }
        }
        return fieldList;
    }


    /**
     * 获取TableId值
     * @param tableInfo
     * @param elem
     * @param <ELEM>
     * @return
     */
    public static <ELEM> String getTableIdValue(TableInfo tableInfo, ELEM elem) {

        Assert.hasText(tableInfo.getKeyProperty(),"主键属性不能为空！");

        Field field = null;
        try {
            field = elem.getClass().getDeclaredField(tableInfo.getKeyProperty());
            field.setAccessible(true);
            return (String) field.get(elem);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成
     * @param field
     * @return
     */
    public static Object generateTableIdValue(Field field) {

        throw new UnsupportedOperationException("不支持的操作");
    }

    public static void setTableFieldValue(Object refObject, Class clazz, Map<String,Object> fieldValueMap) {

        Field[] declaredFields = clazz.getDeclaredFields();
        try {
            for (Field field : declaredFields) {
                field.setAccessible(true);
                for(Map.Entry<String,Object> entry:fieldValueMap.entrySet()){
                    if (field.isAnnotationPresent(TableField.class)) {
                        TableField tableFieldValue = AnnotationUtils.findAnnotation(field, TableField.class);
                        if (entry.getKey().equals(tableFieldValue.value())) {
                            field.set(refObject, entry.getValue());
                        }
                    } else if (field.isAnnotationPresent(TableId.class)) {
                        //toDO 提供TableInfoHelper中生成主键值
                        field.set(refObject,TableInfoHelper.generateTableIdValue(field));
                    }
                }


            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
