package cn.com.unify.db.solr;

import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.CursorMarkParams;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * solr游标查询迭代器
 * @author: fanxiaoning
 * @since v1.0.1
 */
@Slf4j
public class SolrDbIterator implements Iterator {


    private CloudSolrClient cloudSolrClient;

    /**
     * solrQuery查询对象
     */
    private SolrQuery solrQuery;

    /**
     * 游标初始化
     */
    private  String cursorMark;

    private Class<?> elemClass;

    private static  QueryResponse queryResponse;

    public SolrDbIterator(CloudSolrClient cloudSolrClient,SolrQuery solrQuery, Class<?> elemClass) {
        this.cloudSolrClient = cloudSolrClient;
        this.cursorMark =  CursorMarkParams.CURSOR_MARK_START;
        this.solrQuery = solrQuery;
        this.elemClass = elemClass;
    }


    @Override
    public boolean hasNext() {

        String nextCursorMark = null;
        try {
            queryResponse = cloudSolrClient.query(solrQuery);
            nextCursorMark = queryResponse.getNextCursorMark();
        } catch (SolrServerException e) {
            log.error("solr服务异常", e);
        } catch (IOException e) {
            log.error("查询solr结果集异常",e);
        }

        if(cursorMark.equals(nextCursorMark)){
            return true;
        }
        return false;
    }

    @Override
    public Object next() {
        List<?> beans = queryResponse.getBeans(elemClass);
        return beans;
    }
}
