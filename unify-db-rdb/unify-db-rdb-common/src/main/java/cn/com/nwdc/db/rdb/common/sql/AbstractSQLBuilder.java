package cn.com.nwdc.db.rdb.common.sql;

import cn.com.nwdc.db.anno.cache.TableInfoHelper;
import cn.com.nwdc.db.anno.cache.info.TableFieldInfo;
import cn.com.nwdc.db.anno.cache.info.TableInfo;
import cn.com.nwdc.utils.ClassUtil;
import cn.com.nwdc.utils.UUIDUtils;
import com.baomidou.mybatisplus.enums.IdType;
import org.springframework.util.StringUtils;


/**
 * @author coffee
 * @Classname AbstractSQLBuilder
 * @Description TODO
 * @Date 2019/11/5 13:24
 */
public abstract class AbstractSQLBuilder<ELEM> implements ISQLBuilder<ELEM> {


    protected ELEM elem;

    protected TableInfo tableInfo;

    public AbstractSQLBuilder(ELEM elem) {
        this.elem = elem;
        this.tableInfo = TableInfoHelper.getTableInfo(elem.getClass());
    }

    public AbstractSQLBuilder() {
    }

    @Override
    public String keySQL(Class<?> elemClass) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(elemClass);
        String sql = getType().getSql().replace(SQL.TABLE_NAME, tableInfo.getTableName());
        return doKeySQL(sql, tableInfo);

    }


    protected SQL.FieldEntry buildValSql(ELEM elem, SQL.FieldEntry fieldEntry) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(elem.getClass());
        Object value = ClassUtil.getFieldValue(tableInfo.getKeyProperty(), elem);
        if (tableInfo.getIdType() == IdType.UUID
                && StringUtils.isEmpty(value)) {
            fieldEntry.setPkValue(UUIDUtils.getUUID());
        } else {
            fieldEntry.setPkValue(value);
        }
        for (TableFieldInfo fieldInfo : tableInfo.getFieldList()) {
            fieldEntry.getFieldValues().add(ClassUtil.getFieldValue(fieldInfo.getProperty(), elem));
        }
        return fieldEntry;
    }

    protected StringBuffer getKeySQL(TableInfo tableInfo) {
        StringBuffer keySql = new StringBuffer();
        keySql.append(tableInfo.getKeyColumn()).append(",");
        for (TableFieldInfo fieldInfo : tableInfo.getFieldList()) {
            keySql.append(fieldInfo.getColumn()).append(",");
        }
        return keySql.deleteCharAt(keySql.length() - 1);

    }

    protected StringBuffer getParamSQL(TableInfo tableInfo) {
        StringBuffer paramSql = new StringBuffer();

        for (TableFieldInfo tableFieldInfo : tableInfo.getFieldList()) {
            paramSql.append("?,");
        }
        paramSql.deleteCharAt(paramSql.length() - 1);
        return paramSql;
    }



    protected abstract String doKeySQL(String sql, TableInfo tableInfo);



    @Override
    public TableInfo getTableInfo() {
        return tableInfo;
    }

    public void setTableInfo(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }

    public ELEM getElem() {
        return elem;
    }

    @Override
    public void setElem(ELEM elem) {
        this.elem = elem;
        this.tableInfo = TableInfoHelper.getTableInfo(elem.getClass());
    }
}
