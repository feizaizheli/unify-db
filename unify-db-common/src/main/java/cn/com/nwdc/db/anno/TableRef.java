package cn.com.nwdc.db.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author coffee
 * @Classname TableRef
 * @Description TODO
 * @Date 2020/4/14 8:40
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TableRef {

    Casecade cascade() default Casecade.All;

    boolean inverse() default true;

    Class<?> refClass();

    String sourceField();

    String targetField();


}

