package cn.com.nwdc.db.anno.cache.info;

import cn.com.nwdc.db.anno.ITableNameStrategy;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.util.List;

/**
 * @author heffb
 * @Classname TableInfo
 * @Description TODO
 * @Date 2019/10/29 11:09
 * @group smart video north
 */
@Data
public class TableInfo {
    /**
     * 表主键ID 类型
     */
    private IdType idType = IdType.NONE;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 表主键ID 属性名
     */
    private String keyProperty;

    /**
     * 表主键ID 字段名
     */
    private String keyColumn;

    /**
     * 表字段信息列表
     */
    private List<TableFieldInfo> fieldList;

    private ITableNameStrategy tableNameStrategy;



    public String mappingFieldByColumn(String colName) {
        String fieldName = colName;
        if (getKeyColumn().toUpperCase().equals(colName.toUpperCase())) {
            fieldName = getKeyProperty();
        } else {
            for (TableFieldInfo fieldInfo : getFieldList()) {
                if (fieldInfo.getColumn().toUpperCase().equals(colName.toUpperCase())) {
                    fieldName = fieldInfo.getProperty();
                    break;
                }
            }
        }
        return fieldName;

    }




}
