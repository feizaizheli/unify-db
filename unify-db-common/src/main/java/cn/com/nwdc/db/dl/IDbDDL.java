package cn.com.nwdc.db.dl;

import java.io.Closeable;

/**
 * @author coffee
 * @Classname IDbDDL 数据操作语言
 * @Description TODO
 * @Date 2019/10/11 8:50
 */
public interface IDbDDL extends Closeable {



    /**
     * 创建表
     * @param tableName
     */
    void createTable(String tableName);

    /**
     * 删除表
     * @param tableName
     */
    void deleteTable(String tableName);

    /**
     * 清空表
     * @param tableName
     */
    void truncateTable(String tableName);

    /**
     * 表是否存在
     * @param tableName
     * @return
     */
    boolean isExistTable(String tableName);

}
