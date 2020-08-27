package cn.com.nwdc.unify.db.mapper;

import cn.com.nwdc.unify.db.entity.User;
import cn.com.nwdc.unify.db.index.common.IndexDbOperator;
import cn.com.nwdc.unify.db.index.common.anno.IndexMapper;
import cn.com.nwdc.unify.db.index.common.anno.ParamTableName;
import cn.com.nwdc.unify.db.index.common.anno.ParamTableNameStrategyBean;
import cn.com.nwdc.unify.db.pageinfo.PageInfo;
import cn.com.nwdc.unify.db.vo.UserVo;

import java.util.List;
import java.util.Map;

/**
 * @author coffee
 * @Classname UserMapper
 * @Description TODO
 * @Date 2020/7/17 14:23 默认按照接口泛型去取，返回值
 */
@IndexMapper
public interface UserMapper extends IndexDbOperator<User> {

    List<User> queryUserList(String name);


    List<UserVo> queryUserVoList(@ParamTableName String tableName, String name);

    List<UserVo> queryUserVoListByStretegyBean(@ParamTableNameStrategyBean User user, String name);

    List<Map<String,Object>> queryUserList1(@ParamTableName String tableName, String name);




    PageInfo<User> queryUserPageInfo(String name);

}
