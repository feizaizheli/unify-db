package cn.com.nwdc.unify.db.mapper;


import cn.com.nwdc.unify.db.index.common.IndexDbOperator;
import cn.com.nwdc.unify.db.index.common.anno.IndexMapper;
import cn.com.nwdc.unify.db.index.common.anno.ParamTableName;
import cn.com.nwdc.unify.db.index.common.anno.ParamTableNameStrategyBean;
import cn.com.nwdc.unify.db.vo.UserVo;
import cn.com.nwdc.unify.db.entity.AggVo;
import cn.com.nwdc.unify.db.entity.SuperUser;

import java.util.List;
import java.util.Map;

@IndexMapper
public interface SuperUserMapper extends IndexDbOperator<SuperUser> {

    List<UserVo> queryUserVoListByStretegyBean(@ParamTableNameStrategyBean SuperUser user, String name);

    List<Map<String,Object>> queryUserListByGroup(@ParamTableName String tableName, String groupAge, String name);

    List<AggVo> queryUserListByGroup1(@ParamTableName String tableName, String groupAge, String name);
}
