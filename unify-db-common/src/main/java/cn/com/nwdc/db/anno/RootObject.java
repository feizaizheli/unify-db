package cn.com.nwdc.db.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author coffee
 * @Classname RootObject
 * @Description TODO
 * @Date 2020/7/23 14:18
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RootObject {

    String value() ;
}
