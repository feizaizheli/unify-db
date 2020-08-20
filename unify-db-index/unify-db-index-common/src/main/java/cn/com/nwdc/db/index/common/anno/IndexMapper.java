package cn.com.nwdc.db.index.common.anno;

/**
 * @author coffee
 * @Classname ElasticSearchMapper
 * @Description TODO
 * @Date 2020/7/17 19:54
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface IndexMapper {
}
