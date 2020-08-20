package cn.com.nwdc.db.rdb.common.utils;

import cn.com.nwdc.db.anno.cache.TableInfoHelper;
import cn.com.nwdc.db.anno.cache.info.TableFieldInfo;
import cn.com.nwdc.db.anno.cache.info.TableInfo;
import cn.com.nwdc.db.rdb.common.sql.ISQLBuilder;
import cn.com.nwdc.db.rdb.common.sql.SQL;
import cn.com.nwdc.utils.ClassUtil;
import cn.com.nwdc.utils.UUIDUtils;
import com.baomidou.mybatisplus.enums.IdType;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;

/**
 * @author coffee
 * @Classname SQLUtils
 * @Description TODO
 * @Date 2020/3/6 18:50
 */
public class SQLUtils {




    public static <ELEM> String insertSql(ELEM elem){
        ISQLBuilder<ELEM> insertSQL = SQL.INSERT.build(elem);
        String insertKeySQL = insertSQL.keySQL(elem.getClass());
        insertKeySQL = insertKeySQL.substring(0,insertKeySQL.indexOf(SQL.SQL_VALUES));
        return insertKeySQL + SQLUtils.getValSql(elem);
    }








    public static <ELEM> String getValSql(ELEM elem){
        TableInfo tableInfo = TableInfoHelper.getTableInfo(elem.getClass());
        Object keyValue = ClassUtil.getFieldValue(tableInfo.getKeyProperty(), elem);

        StringBuilder builder = new StringBuilder();
        if (tableInfo.getIdType() == IdType.UUID && StringUtils.isEmpty(keyValue)) {
            keyValue = UUIDUtils.getUUID();
        }

        Field field = ClassUtil.getField(tableInfo.getKeyProperty(),elem);
        if(field.getType() == String.class){
            builder.append(SQL.SQL_VALUES).append("'").append(keyValue).append("',");
        }else{
            builder.append(SQL.SQL_VALUES).append(keyValue).append(",");
        }

        for (TableFieldInfo fieldInfo : tableInfo.getFieldList()) {
            Object propertyVal = ClassUtil.getFieldValue(fieldInfo.getProperty(), elem);
            if((String.class == fieldInfo.getField().getType() || java.util.Date.class == fieldInfo.getField().getType()) && propertyVal!=null) {
                builder.append("'").append(propertyVal).append("',");
            }else {
                builder.append(ClassUtil.getFieldValue(fieldInfo.getProperty(), elem)).append(",");
            }
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append(");");
        return builder.toString();
    }
}
