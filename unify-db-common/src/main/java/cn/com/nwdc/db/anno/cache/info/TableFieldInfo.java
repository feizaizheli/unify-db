package cn.com.nwdc.db.anno.cache.info;


import cn.com.nwdc.db.anno.Casecade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;

/**
 * @author heffb
 * @Classname TableFieldInfo
 * 数据库表字段反射信息
 * @Description TODO
 * @Date 2019/10/29 11:10
 * @group smart video north
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableFieldInfo {
    /**
     * 数据库字段名
     */
    private String column;

    /**
     * 属性名
     */
    private String property;

    /**
     * filed
     */
    private Field field;


    private TableRefInfo tableRef;

    public TableFieldInfo(String column, String property, Field field) {
        this.column = column;
        this.property = property;
        this.field = field;
    }

    public void initTableRefInfo(Class refClass, String sourceField, String targetField, boolean inverse, Casecade cascade, boolean isCollection,
                                         TableInfo tableInfo, TableInfo refTableInfo){

        this.tableRef = new TableRefInfo(refClass,sourceField,targetField,inverse,cascade,isCollection,
                tableInfo,refTableInfo);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class TableRefInfo{

        private Class refClass;
        private String sourceField;
        private String targetField;
        private boolean inverse;
        private Casecade cascade;
        private boolean isCollection;

        private TableInfo targetTableInfo;

        private TableInfo refTableInfo;
    }


}
