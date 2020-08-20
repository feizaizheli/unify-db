package cn.com.nwdc.db.rdb.common.type.adapter;

/**
 * @author coffee
 * @Classname TypeAdapterString
 * @Description TODO
 * @Date 2020/7/15 15:42
 */
public class TypeAdapterString  extends AbstractTypeAdapter<String>{

    @Override
    public Integer getInteger(String s) {
        return Integer.parseInt(s);
    }

    @Override
    public String getString(String s) {
        return super.getString(s);
    }

    @Override
    public Float getFloat(String s) {
        return Float.parseFloat(s);
    }
}
