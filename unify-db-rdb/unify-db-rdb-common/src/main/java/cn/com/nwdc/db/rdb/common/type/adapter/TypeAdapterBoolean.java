package cn.com.nwdc.db.rdb.common.type.adapter;

/**
 * @author coffee
 * @Classname BooleanAdapter
 * @Description TODO
 * @Date 2020/7/15 14:19
 */
public class TypeAdapterBoolean extends AbstractTypeAdapter<Boolean> {


    @Override
    public Boolean getBoolean(Boolean value) {
        return null;
    }

    @Override
    public Integer getInteger(Boolean value) {
        if(value){
            return 1;
        }else{
            return 0;
        }

    }

    @Override
    public Float getFloat(Boolean value) {
        if(value){
            return 1.0F;
        }else{
            return 0.0F;
        }
    }

    @Override
    public Double getDouble(Boolean value) {
        if(value){
            return 1.0D;
        }else{
            return 0.0D;
        }
    }


    @Override
    public String getString(Boolean aBoolean) {
        return super.getString(aBoolean);
    }
}
