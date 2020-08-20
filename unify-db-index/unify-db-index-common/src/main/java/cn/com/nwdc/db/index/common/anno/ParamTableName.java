package cn.com.nwdc.db.index.common.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author coffee
 * @Classname ParamTableName
 * @Description TODO
 * @Date 2020/7/19 17:35
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface ParamTableName {


}
