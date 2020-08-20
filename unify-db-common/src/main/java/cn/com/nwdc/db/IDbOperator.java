package cn.com.nwdc.db;



import cn.com.nwdc.db.condition.DbCondition;
import cn.com.nwdc.db.dl.IDbDDL;
import cn.com.nwdc.db.dl.IDbDML;

import java.util.List;

/**
 * @author coffee
 * @Classname IDbOperator
 * @Description TODO
 * @Date 2019/9/15 17:18
 */
public interface IDbOperator<ELEM , CONDITION extends DbCondition> extends IDbDDL, IDbDML<ELEM,CONDITION> {


    //批量插入，批量插入失败，尝试遍历单条插入

    /**
     *
     * @param elemList
     * @param isDuplicateRetryIterSingle 是否尝试遍历单条插入
     */
    public void batchSaveRepairDuplicate(List<ELEM> elemList, boolean isDuplicateRetryIterSingle);






}
