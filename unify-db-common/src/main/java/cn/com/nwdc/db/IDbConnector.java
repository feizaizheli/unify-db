package cn.com.nwdc.db;

import cn.com.nwdc.db.condition.DbCondition;

import java.io.Closeable;
import java.util.Properties;

/**
 * @author coffee
 * @Classname IDbConnector
 * @Description TODO
 * @Date 2019/9/15 17:43
 */
public interface IDbConnector<DATA_SOURCE> extends Closeable {

    default void connect(Properties properties){};

    default void setDataSource(DATA_SOURCE dbInfo){};

    default <ELEM,CONDITION extends DbCondition> IDbOperator<ELEM,CONDITION> createOperator(){return null;};

   // void setDataSource(DataSource dataSource);

}
