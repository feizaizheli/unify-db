package cn.com.nwdc.db.pageinfo;

import java.util.Set;

/**
 * @author heffb
 * @Classname IGroupPageFilter
 * @Description TODO
 * @Date 2019/10/8 14:01
 * @group smart video north
 */
public interface IGroupPageFilter<RESPONSE> {
    public RESPONSE filter(Set<String> keys);
}
