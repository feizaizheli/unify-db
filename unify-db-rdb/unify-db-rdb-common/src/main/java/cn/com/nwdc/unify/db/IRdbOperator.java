package cn.com.nwdc.unify.db;

import cn.com.nwdc.unify.db.condition.DbCondition;

import javax.sql.DataSource;

/**
 * @author coffee
 * @Classname IRdbOperator
 * @Description TODO
 * @Date 2019/11/5 10:14
 */
public interface IRdbOperator<ELEM> extends IDbOperator<ELEM, DbCondition<String>>,IDbConnector<DataSource> {





}
