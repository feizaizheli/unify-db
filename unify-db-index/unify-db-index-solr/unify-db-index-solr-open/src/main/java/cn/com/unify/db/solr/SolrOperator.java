package cn.com.unify.db.solr;


import cn.com.nwdc.db.AbstractDbOperator;
import cn.com.nwdc.db.condition.DbCondition;
import cn.com.nwdc.db.pageinfo.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author coffee
 * @Classname SolrOperator
 * @Description TODO
 * @Date 2019/10/23 16:19
 */
@Slf4j
@Component("solrOperator")
public class SolrOperator<ELEM> extends AbstractDbOperator<ELEM, DbCondition<SolrQuery>> {

    @Autowired
    private CloudSolrClient cloudSolrClient;

    /**
     * 游标查询返回迭代器
     * @param condition SolrQuery必须设置主键排序
     * @return
     */
    @Override
    public Iterator<ELEM> iterator(DbCondition<SolrQuery> condition) {
        return new SolrDbIterator(cloudSolrClient,condition.getSql(),condition.getElemClass());
    }

    @Override
    public PageInfo<ELEM> queryForPage(DbCondition<SolrQuery> condition, PageInfo<ELEM> pageInfo) {
        SolrQuery solrQuery = condition.getSql();

        //初始化分页参数
        int pageIndex = 0;
        int pageSize = 0;

        if (pageInfo != null) {
            pageIndex = pageInfo.getPageIndex();
            pageSize = pageInfo.getPageSize();

            //设置分页参数
            solrQuery.setStart((pageIndex - 1) * pageSize);
            //每一页多少数据
            solrQuery.setRows(pageSize);
        }

        QueryResponse query = null;
        try {
            query = cloudSolrClient.query(solrQuery);
        } catch (SolrServerException e) {
            log.error("solr服务异常", e);
        } catch (IOException e) {
            log.error("插入java对象到solr库异常", e);
        }

        List<?> beans = query.getBeans(condition.getElemClass());

        //查询的总记录数
        long totalCount = beans.size();

        //分页类
        pageInfo.setDataList((List<ELEM>)beans);
        pageInfo.setTotalCount(totalCount);
        pageInfo.setPageIndex(pageIndex);
        pageInfo.setPageSize(pageSize);

        return pageInfo;
    }

    @Override
    public PageInfo<Map<String, Object>> queryForMapPage(DbCondition<SolrQuery> condition, PageInfo<Map<String, Object>> pageInfo) {
        return null;
    }


    @Override
    public Object rawExecute(DbCondition<SolrQuery> condition) {
        return null;
    }




    /**
     * 批量插入java对象到索引库
     * @param elems
     */
    @Override
    protected void doBatchSave(List<ELEM> elems) {
        try {
            cloudSolrClient.addBeans(elems);
        } catch (SolrServerException e) {
            log.error("solr服务异常", e);
        } catch (IOException e) {
            log.error("插入java对象到solr库异常", e);
        }
    }


    /**
     * 插入java对象到索引库
     * @param elem
     */
    @Override
    public int save(ELEM elem) {

        try {
            cloudSolrClient.addBean(elem);
        } catch (IOException e) {
            log.error("插入java对象到solr库异常", e);
        } catch (SolrServerException e) {
            log.error("solr服务异常", e);
        }

        return 1;
    }

    /**
     * 根据id删除
     * @param id
     */
    @Override
    public int delete(Serializable id, Class<?> elemClass) {
        Assert.notNull(id,"id is empty");
        try {
            cloudSolrClient.deleteById((String)id);
        } catch (SolrServerException e) {
            log.error("solr服务异常", e);
        } catch (IOException e) {
            log.error("根据id删除solr库索引异常", e);
        }
        return 1;
    }



    /**
     * 根据ids批量删除
     * @param ids
     */
    @Override
    public int delete(List<Serializable> ids,Class<?> elemClass) {
        return 1;
    }

    /**
     * 批量修改
     * @param elems
     */
    @Override
    public int update(List<ELEM> elems) {

        try {
            cloudSolrClient.addBeans(elems);
        } catch (IOException e) {
            log.error("插入java对象到solr库异常", e);
        } catch (SolrServerException e) {
            log.error("solr服务异常", e);
        }
        return 1;
    }

    @Override
    public int update(ELEM elem, Class<?> elemClass) {
        return 1;
    }

    @Override
    public ELEM findElemById(Serializable id, Class<?> elemClass) {
        return null;
    }

    @Override
    public ELEM findElemByCondition(DbCondition<SolrQuery> condition, Class<?> elemClass) {
        return null;
    }

    @Override
    public List<Map<String, Object>> queryForMapList(DbCondition<SolrQuery> condition) {
        return null;
    }

    @Override
    public List<ELEM> queryForList(DbCondition<SolrQuery> condition) {
        return null;
    }


    @Override
    public void createTable(String tableName) {

    }

    @Override
    public void deleteTable(String tableName) {

    }

    @Override
    public void truncateTable(String tableName) {

    }

    @Override
    public boolean isExistTable(String tableName) {
        return false;
    }

    @Override
    public void close() throws IOException {

    }
}
