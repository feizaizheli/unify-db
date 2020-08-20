package cn.com.nwdc.db.rdb.common.type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author coffee
 * @Classname TypeAdapterResolver
 * @Description TODO
 * @Date 2020/7/15 14:59
 */
public class TypeAdapterResolver {


    private static final Logger LOGGER = LoggerFactory.getLogger(TypeAdapterResolver.class);


    public static String TYPE_GETTER = "get";

    public static Object getValue(Class<?> sourceClass,Class<?> targetClass,Object value) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        ITypeAdapter typeAdapter = TypeAdapterFactory.getTypeAdapter(sourceClass);

        if(null == typeAdapter){
            LOGGER.warn("[Type Adapter:{}] is not exist!,return raw value",sourceClass.getSimpleName());
            return value;
        }

        Method typeGetter = typeAdapter.getClass().getDeclaredMethod(TYPE_GETTER+targetClass.getSimpleName(),sourceClass);
        return typeGetter.invoke(typeAdapter,value);

    }
}
