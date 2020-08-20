package cn.com.nwdc.db.rdb.common.type;

import cn.com.nwdc.db.rdb.common.type.adapter.TypeAdapterBoolean;
import cn.com.nwdc.db.rdb.common.type.adapter.TypeAdapterInteger;
import cn.com.nwdc.db.rdb.common.type.adapter.TypeAdapterString;
import cn.com.nwdc.db.rdb.common.type.adapter.TypeAdapterTimestamp;
import com.google.common.collect.Maps;

import java.sql.Timestamp;
import java.util.Map;

/**
 * @author coffee
 * @Classname TypeAdapterFactory
 * @Description TODO
 * @Date 2020/7/15 14:24
 */
public class TypeAdapterFactory {


    public static Map<Class<?>, ITypeAdapter> typeAdapterMap = Maps.newHashMap();

    static{
        typeAdapterMap.put(Boolean.class,new TypeAdapterBoolean());
        typeAdapterMap.put(Timestamp.class,new TypeAdapterTimestamp());
        typeAdapterMap.put(String.class,new TypeAdapterString());
        typeAdapterMap.put(Integer.class,new TypeAdapterInteger());
    }


    public static ITypeAdapter getTypeAdapter(Class<?> typeClass){
        return typeAdapterMap.get(typeClass);

    }




}
