package cn.com.nwdc.db.rdb.common.type;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * @author coffee
 * @Classname ITypeHandler
 * @Description TODO
 * @Date 2020/7/15 14:15
 */
public interface ITypeAdapter<VALUE> {



    Boolean getBoolean(VALUE value);

    Integer getInteger(VALUE value);

    Float getFloat(VALUE value);

    Double getDouble(VALUE value);

    Date getDate(VALUE value);

    Timestamp getTimeStamp(VALUE value);

    String getString(VALUE value);








}
