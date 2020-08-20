package cn.com.nwdc.db.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author coffee
 * @Classname TableNameStrategy
 * @Description TODO
 * @Date 2020/7/17 9:13
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TableNameStrategy {

    Class<? extends ITableNameStrategy> value() ;

    //EnumDbType dbType() default EnumDbType.UNKOWN;


}
