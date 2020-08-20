package cn.com.nwdc.db.rdb.common.type.adapter;



import cn.com.nwdc.db.rdb.common.type.ITypeAdapter;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * @author coffee
 * @Classname AbstractTypeAdapter
 * @Description TODO
 * @Date 2020/7/15 14:41
 */
public abstract class AbstractTypeAdapter<VALUE> implements ITypeAdapter<VALUE> {
    @Override
    public Boolean getBoolean(VALUE value) {
        throw new UnsupportedOperationException("unsupport type convert :"+value.getClass().getTypeName()+"-> Boolean");
    }

    @Override
    public Integer getInteger(VALUE value) {
        throw new UnsupportedOperationException("unsupport type convert :"+value.getClass().getTypeName()+"-> Integer");
    }

    @Override
    public Float getFloat(VALUE value) {
        throw new UnsupportedOperationException("unsupport type convert :"+value.getClass().getTypeName()+"-> Float");
    }

    @Override
    public Double getDouble(VALUE value) {
        throw new UnsupportedOperationException("unsupport type convert :"+value.getClass().getTypeName()+"-> Double");
    }

    @Override
    public Date getDate(VALUE value) {
        throw new UnsupportedOperationException("unsupport type convert :"+value.getClass().getTypeName()+"-> Timestamp");
    }

    @Override
    public Timestamp getTimeStamp(VALUE value) {
        throw new UnsupportedOperationException("unsupport type convert :"+value.getClass().getTypeName()+"-> Timestamp");
    }


    @Override
    public String getString(VALUE value) {

        return String.valueOf(value);
    }

}
