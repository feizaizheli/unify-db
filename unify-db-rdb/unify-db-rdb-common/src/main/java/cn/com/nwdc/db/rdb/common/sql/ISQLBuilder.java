package cn.com.nwdc.db.rdb.common.sql;

import cn.com.nwdc.db.anno.cache.info.TableInfo;

/**
 * @author coffee
 * @Classname ISQLBuilder
 * @Description TODO
 * @Date 2019/11/5 13:22
 */
public interface ISQLBuilder<ELEM> {

    /**
     * 获取主干SQL
     * @param elemClass
     * @return
     */
    String keySQL(Class<?> elemClass);

    /**
     * 获取主干赋值SQL
     * @param elem
     * @return
     */
    SQL.FieldEntry valSQL(ELEM elem);

    /**
     * 获取SQL类型
     * @return
     */
    SQL.Type  getType();


    public String keyFieldSQL();

    public String valFieldSQL(String valPrefix, String valEnd);

    TableInfo getTableInfo();


    void setElem(ELEM elem);
}