package cn.com.nwdc.db.rdb.common.type.adapter;

import java.sql.Timestamp;

/**
 * @author coffee
 * @Classname TypeAdapterTimestamp
 * @Description TODO
 * @Date 2020/7/15 14:43
 */
public class TypeAdapterTimestamp  extends AbstractTypeAdapter<Timestamp> {


    @Override
    public String getString(Timestamp timestamp) {
        return String.valueOf(timestamp);
    }


}
