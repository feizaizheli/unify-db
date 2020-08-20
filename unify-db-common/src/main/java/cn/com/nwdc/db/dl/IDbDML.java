package cn.com.nwdc.db.dl;



import cn.com.nwdc.db.condition.DbCondition;
import cn.com.nwdc.db.elem.IDbElemFilter;
import cn.com.nwdc.db.pageinfo.PageInfo;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author coffee
 * @Classname IDbDML    数据定义语言
 * @Description TODO
 * @Date 2019/11/5 10:09
 */
public interface IDbDML<ELEM , CONDITION extends DbCondition> {

    //保存
    void batchSave(Iterator<ELEM> elemIterator, IDbElemFilter<ELEM>... elemFilters);
    void batchSave(List<ELEM> elemList, IDbElemFilter<ELEM>... elemFilters);
    int save(ELEM elem);

    //删除
    int delete(Serializable id, Class<?> elemClass);
    int delete(List<Serializable> ids, Class<?> elemClass);

    //更新
    int update(List<ELEM> elemList);
    int update(ELEM elem, Class<?> elemClass);


    int saveOrUpdate(List<ELEM> elemList);
    int saveOrUpdate(ELEM elem, Class<?> elemClass);

    //查询-单条
    ELEM findElemById(Serializable id, Class<?> elemClass) ;
    ELEM findElemByCondition(CONDITION condition, Class<?> elemClass);


    //查询-集合
    List<Map<String,Object>> queryForMapList(CONDITION condition);
    List<ELEM> queryForList(CONDITION condition);

    Iterator<ELEM> iterator(CONDITION condition);
    Iterator<Map<String,Object>> iteratorMap(CONDITION condition);

    PageInfo<ELEM> queryForPage(CONDITION condition, PageInfo<ELEM> pageInfo);
    PageInfo<Map<String,Object>> queryForMapPage(CONDITION condition, PageInfo<Map<String,Object>> pageInfo);

    //原生查询
    Object rawExecute(CONDITION condition);

    /**参数设置*/
    void setFetchSize(int fetchSize);


}
