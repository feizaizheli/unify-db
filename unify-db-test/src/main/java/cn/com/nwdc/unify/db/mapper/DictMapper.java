package cn.com.nwdc.unify.db.mapper;

import cn.com.nwdc.unify.db.index.common.anno.IndexMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author coffee
 * @Classname DictMapper
 * @Description TODO
 * @Date 2020/8/26 9:24
 */

@IndexMapper
public interface DictMapper {

    /**
     * 检测字段值，单值重查（防止前端注入）
     *
     * @author chenyda
     * @date 2020/8/21 9:39
     *
     * @param systemType
     * @return java.lang.String
     */
    String getSqlColumnBySystemType(@Param("systemType") String systemType);
}
