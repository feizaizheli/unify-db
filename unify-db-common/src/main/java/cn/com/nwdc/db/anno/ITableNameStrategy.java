package cn.com.nwdc.db.anno;

/**
 * @author coffee
 * @Classname ITableNameStrategy
 * @Description TODO
 * @Date 2020/7/17 16:04
 */
public interface ITableNameStrategy<ELEM> {


    String getTableName(String tableName, ELEM elem) ;
}
