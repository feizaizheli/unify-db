package cn.com.nwdc.db.index.common;


import cn.com.nwdc.db.IDbOperator;
import org.apache.http.Header;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Map;

/**
 * @author coffee
 * @Classname IESDbOperator
 * @Description TODO
 * @Date 2020/7/17 14:41
 */
public interface IndexDbOperator<ELEM> extends IDbOperator<ELEM, IndexDbCondition> {




    String alias(String aliasName, String indexName);

    String getAlias(String indexName);

    int totalCount(IndexDbCondition esDbCondition);

    String deleteByQuery(IndexDbCondition esDbCondition) ;

    String refresh(String indexName);

    void createTable(IndexDbCondition esDbCondition);

    List<Map<String,Object>> queryAllIndex(String indexName);

    String postRestRequest(String endpoint, String jsonEntity);

    String executeRestRequest(HttpMethod methodd, String endpoint, String jsonEntity);

    String executeRestRequest(HttpMethod method, String endpoint, Map params, String jsonEntity, Header... headers) ;



}
