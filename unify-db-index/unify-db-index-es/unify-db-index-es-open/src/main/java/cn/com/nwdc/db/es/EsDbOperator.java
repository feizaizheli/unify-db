package cn.com.nwdc.db.es;


import cn.com.nwdc.db.AbstractDbOperator;
import cn.com.nwdc.db.anno.cache.TableInfoHelper;
import cn.com.nwdc.db.anno.cache.info.TableInfo;
import cn.com.nwdc.db.elem.DbElement;
import cn.com.nwdc.db.index.common.IndexDbCondition;
import cn.com.nwdc.db.index.common.IndexDbTemplate;
import cn.com.nwdc.db.index.common.IndexException;
import cn.com.nwdc.db.index.common.IndexDbOperator;
import cn.com.nwdc.db.index.common.utils.EsBeanUtils;
import cn.com.nwdc.db.es.config.EsConfig;
import cn.com.nwdc.db.pageinfo.PageInfo;
import cn.com.nwdc.utils.MapUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.com.nwdc.db.index.common.utils.EsBeanUtils.toDbElement;

/**
 * @author zhngyr
 * @Classname EsStatement
 * @Description TODO
 * @Date 2019/10/11 10:48
 */

@Component("esDbOperator")
public class EsDbOperator<ELEM> extends AbstractDbOperator<ELEM, IndexDbCondition> implements IndexDbOperator<ELEM> {


    public static final String EXECUTE_INDEX = "index";
    public static final String EXECUTE_UPDATE = "update";
    public static final String ES_SQL_ALIASES = "/_aliases";
    protected static Logger LOGGER = LoggerFactory.getLogger(EsDbOperator.class);

    @Autowired
    protected RestClient restClient;


    @Autowired
    protected EsConfig esConfig;




    @PostConstruct
    public void init() throws IOException {
        IndexDbTemplate.loadResources(esConfig.getTemplatePath());
    }

    @Override
    protected void doBatchSave(List<ELEM> elemList) {

        doBulk(elemList, EXECUTE_INDEX);

    }



    private void doBulk(List<ELEM> elemList,String updateFlag) {
        StringBuilder bulkRequestBody = new StringBuilder();
        String indexName = null;
        DbElement dbElement = null;
        TableInfo tableInfo = null;

        for (ELEM elem : elemList) {
            if (elem instanceof DbElement) {
                dbElement = (DbElement) elem;

            } else {
                dbElement = toDbElement(elem);
            }
            if(indexName==null || tableInfo.getTableNameStrategy()!=null){
                tableInfo = TableInfoHelper.getTableInfo(elem.getClass());
                indexName = getIndexName(elem);
            }
            String dbElementJson = JSON.toJSONString(dbElement.getProperties());
            String actionMetaData = String.format("{ \""+updateFlag+"\" : { \"_index\" : \"%s\", \"_type\" : \"%s\" ,\"_id\" : \"%s\"} }%n", indexName, esConfig.getFiType(), dbElement.getId());
            bulkRequestBody.append(actionMetaData);

            if(EXECUTE_UPDATE.equals(updateFlag)){
                bulkRequestBody.append("{\"doc\": ");
                bulkRequestBody.append(dbElementJson);
                bulkRequestBody.append("}");
            }else{
                bulkRequestBody.append(dbElementJson);
            }

            bulkRequestBody.append("\n");
        }

        executeRestRequest(HttpMethod.POST,"/"+indexName+"/"+esConfig.getFiType()+"/_bulk",bulkRequestBody.toString());
    }

    private String getIndexName(ELEM elem){

       return getIndexName(elem.getClass(),elem);

    }

    private String getIndexName(Class<?> elemClass,ELEM elem){
        String indexName = null;

        if(elemClass !=null){
            TableInfo tableInfo = TableInfoHelper.getTableInfo(elemClass);
            if(tableInfo.getTableNameStrategy()!=null){
                return tableInfo.getTableNameStrategy().getTableName(tableInfo.getTableName(),elem);
            }
            if(tableInfo!=null){
                return tableInfo.getTableName();
            }


        }

        return indexName;

    }
    @Override
    public void createTable(String tableName) {

        String jsonString = "{ \"settings\":{" + "\"number_of_shards\":\"" + esConfig.getShardNum() + "\","
                + "\"number_of_replicas\":\"" + esConfig.getReplicaNum() + "\"" + "},\"mappings\": {\"_default_\": {" +
                "      \"dynamic_templates\": [{\"search_analyzer\": {\"match\": \"*\",\"match_mapping_type\": \"string\"," +
                "            \"mapping\": {\"copy_to\": \"kg\",\"store\": true,\"type\": \"keyword\"}}}]," +
                "      \"properties\": {\"kg\": {\"type\": \"text\",\"store\": true}}}}}";



        executeRestRequest(HttpMethod.PUT,"/" + tableName,jsonString);

    }

    @Override
    public void deleteTable(String tableName) {
        if (isExistTable(tableName)) {
            executeRestRequest(HttpMethod.DELETE,"/" + tableName,null);
        }
    }

    @Override
    public void close() throws IOException {
        if (restClient != null) {
            restClient.close();
        }
    }



    @Override
    public int save(ELEM elem) {
        DbElement dbElement = EsBeanUtils.toDbElement(elem);
        String dbElementJson = JSON.toJSONString(dbElement.getProperties());
        postRestRequest(
                "/" + getIndexName(elem) + "/" + esConfig.getFiType() + "/" + dbElement.getId(),
                dbElementJson
        );
        return 1;

    }

    @Override
    public int update(ELEM elem, Class<?> elemClass) {


        DbElement dbElement = toDbElement(elem);
        String dbElementJson = JSON.toJSONString(dbElement.getProperties());

        String jsonString = "{\"doc\" : " + dbElementJson + "}";

        postRestRequest(
                "/" + getIndexName(elem) + "/" + esConfig.getFiType() + "/" + dbElement.getId() + "/_update",
                jsonString

        );
        return 1;
    }

    @Override
    public PageInfo<ELEM> queryForPage(IndexDbCondition condition, PageInfo<ELEM> pageInfo) {

        try {
            String resultEntity = postRestRequest(condition);
            List<ELEM> elemList = EsBeanUtils.parseReponseJson(
                    resultEntity,(ELEM) condition.getElemClass().newInstance(),condition.getRootObject());
            pageInfo.setDataList(elemList);
            pageInfo.setTotalCount(EsBeanUtils.getTotalCount(resultEntity));
        }catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("parse error!",e);
            throw new RuntimeException("parse error!");
        }
        return pageInfo;
    }

    @Override
    public PageInfo<Map<String, Object>> queryForMapPage(IndexDbCondition condition, PageInfo<Map<String, Object>> pageInfo) {
        try {
            String resultEntity = postRestRequest(condition);
            List<Map<String,Object>> elemList = EsBeanUtils.parseReponseJson(
                    resultEntity,(Map<String,Object>) condition.getElemClass().newInstance(),condition.getRootObject());
            pageInfo.setDataList(elemList);
            pageInfo.setTotalCount(EsBeanUtils.getTotalCount(resultEntity));
        }catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("parse error!",e);
            throw new RuntimeException("parse error!");
        }
        return pageInfo;
    }


    private String getIndexNameByCondition(IndexDbCondition condition){

        if(StringUtils.isNotBlank(condition.getTableName())){
            return condition.getTableName();
        }
        if(condition.getElemClass()!= null){
            return getIndexName(condition.getElemClass(),null);
        }
        throw new RuntimeException("index load error");


    }
    @Override
    public Object rawExecute(IndexDbCondition condition) {


        return postRestRequest(
                "/" + getIndexNameByCondition(condition) + "/" + esConfig.getFiType() + "/_search",
                condition.getSql() );

    }

    @Override
    public int delete(Serializable id, Class<?> elemClass) {


        executeRestRequest(HttpMethod.DELETE,"/" + getIndexName(elemClass,null) +"/" + esConfig.getFiType() +"/"+id,null);
        return 1;
    }

    @Override
    public int delete(List<Serializable> ids, Class<?> elemClass) {
        StringBuilder bulkRequestBody = new StringBuilder();
        String indexName = getIndexName(elemClass,null);
        if (ids.size()>0) {
            for (Serializable id:ids){
                String actionMetaData = String.format(
                        "{ \"delete\" : { \"_index\" : \"%s\", \"_type\" : \"%s\" ,\"_id\" : \"%s\"} }%n",
                        indexName, esConfig.getFiType(), id);
                bulkRequestBody.append(actionMetaData);
                bulkRequestBody.append("\n");
            }
        }

        executeRestRequest(HttpMethod.POST,"/_bulk",bulkRequestBody.toString());
        return 1;
    }

    @Override
    public int update(List<ELEM> dbElements) {

        doBulk(dbElements, EXECUTE_UPDATE);
        return 1;
    }


    @Override
    public boolean isExistTable(String tableName) {
        try {
            executeRestRequest(HttpMethod.GET,"/" + tableName,null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }


    @Override
    public void truncateTable(String tableName) {
        throw new UnsupportedOperationException("暂时不支持");
    }



    @Override
    public ELEM findElemById(Serializable id, Class<?> elemClass) {
        Assert.isNull(id,"id is empty");

        String indexName = getIndexName(elemClass,null);
        String endPoint = indexName+"/"+esConfig.getFiType()+"/"+id;
        String resultEntity = executeRestRequest(HttpMethod.GET,endPoint,null,null);
        JSONObject resultObject = JSONObject.parseObject(resultEntity);
        if(resultObject!=null){
            JSONObject sourceObject = resultObject.getJSONObject("_source");
            if(sourceObject!=null){
                return (ELEM) JSONObject.toJavaObject(sourceObject, elemClass);
            }
        }
        return null;
    }

    @Override
    public ELEM findElemByCondition(IndexDbCondition condition, Class<?> elemClass) {
        List list = queryForList(condition);
        if(list!=null && !list.isEmpty()){
            return (ELEM)list.get(0);
        }
        return null;
    }



    @Override
    public List<Map<String, Object>> queryAllIndex(String indexName) {

        String resultEntity = executeRestRequest(HttpMethod.GET,
                EsParams.CAT_INDICES,
                null,
                null);
        JSONArray jsonArray = JSONArray.parseArray(resultEntity);

        List<Map<String,Object>> esIndexList = null;
        if (jsonArray != null) {
            try {
                esIndexList = EsBeanUtils.loadElemList( jsonArray, new HashMap<>(),null);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if(StringUtils.isNotBlank(indexName)){
            esIndexList = esIndexList.stream().filter(x->{
                return String.valueOf(x.get("index")).indexOf(indexName)>=0;
            }).collect(Collectors.toList());
        }


        if(esIndexList!=null){
            MapUtils.sort(esIndexList,"index");
        }



        return esIndexList;
    }

    @Override
    public List<Map<String,Object>> queryForMapList(IndexDbCondition condition) {

        return EsBeanUtils.parseReponseJson(postRestRequest(condition),
                new HashMap<>(),condition.getRootObject());
    }

    @Override
    public List<ELEM> queryForList(IndexDbCondition condition) {
        return (List<ELEM>) EsBeanUtils.parseReponseJson(postRestRequest(condition),
                new HashMap<>(),condition.getRootObject());
    }


    @Override
    public Iterator<ELEM> iterator(IndexDbCondition condition) {
        throw new UnsupportedOperationException("暂时不支持");
    }

    @Override
    public Iterator<Map<String, Object>> iteratorMap(IndexDbCondition condition) {
        throw new UnsupportedOperationException("暂时不支持");
    }

    @Override
    public String alias(String aliasName, String indexName) {

        String jsonEntity = "{\n" +
                "    \"actions\" : [\n" +
                "        { \"add\" : { \"index\" : \""+indexName+"\", \"alias\" : \""+aliasName+"\" } }\n" +
                "    ]\n" +
                "}";

        return executeRestRequest(HttpMethod.POST, ES_SQL_ALIASES,jsonEntity);

    }

    @Override
    public String getAlias(String indexName) {

        return executeRestRequest(HttpMethod.GET, indexName+"/_alias",null);
    }

    @Override
    public int totalCount(IndexDbCondition esDbCondition) {

        String resultEntiry = postRestRequest(esDbCondition);

        return EsBeanUtils.getTotalCount(resultEntiry);

    }

    @Override
    public String deleteByQuery(IndexDbCondition esDbCondition)  {

        String endPoint = "/"+getIndexNameByCondition(esDbCondition)+"/"+esConfig.getFiType()+ EsParams.DELETE_BY_QUERY;
        if (StringUtils.isNotBlank(esDbCondition.getExtendUrl())){
            endPoint = endPoint + esDbCondition.getExtendUrl();
        }
        return executeRestRequest(HttpMethod.POST,endPoint,null,esDbCondition.getSql());
    }

    @Override
    public void createTable(IndexDbCondition esDbCondition)  {
        String endPoint = "/"+getIndexNameByCondition(esDbCondition);
        if (StringUtils.isNotBlank(esDbCondition.getExtendUrl())){
            endPoint = endPoint + esDbCondition.getExtendUrl();
        }
        try{
            executeRestRequest(HttpMethod.PUT,endPoint,null,esDbCondition.getSql());
        } catch (Exception e) {
            LOGGER.error("ElasticSearch createTable:[{}] exception,{}",endPoint,e);
            throw new IndexException("ElasticSearch createTable exception");
        }
    }



    @Override
    public String refresh(String index) {
        String endpoint = "/" + index  + "/_refresh";
        return executeRestRequest(HttpMethod.POST, endpoint, null, null);
    }


    public String postRestRequest(IndexDbCondition esDbCondition )  {
        String endpoint = "/" + getIndexNameByCondition(esDbCondition) + "/" + esConfig.getFiType() + "/_search";
        if (StringUtils.isNotBlank(esDbCondition.getExtendUrl())){
            endpoint = endpoint + esDbCondition.getExtendUrl();
        }

        return postRestRequest(endpoint,esDbCondition.getSql());

    }
    @Override
    public String postRestRequest(String endpoint, String jsonEntity )  {
        return executeRestRequest(HttpMethod.POST, endpoint, null, jsonEntity);
    }

    @Override
    public String executeRestRequest(HttpMethod method,String endpoint, String jsonEntity )  {
        return executeRestRequest(method, endpoint, null, jsonEntity);
    }
    @Override
    public String executeRestRequest(HttpMethod method, String endpoint, Map params, String jsonEntity, Header...headers )  {
        Response response = null;
        if(params == null){
            params = Maps.newHashMap();
        }
        params.put("pretty", "true");
        NStringEntity entity = null;
        if(StringUtils.isNotBlank(jsonEntity)){
            LOGGER.debug(method.name()+" " + endpoint +" "+ jsonEntity);
            entity = new NStringEntity(jsonEntity, ContentType.APPLICATION_JSON);
        }

        try {
            response = this.restClient.performRequest(method.name(), endpoint, params, entity, new Header[0]);
            if(HttpStatus.SC_OK == response.getStatusLine().getStatusCode()
                    || HttpStatus.SC_CREATED == response.getStatusLine().getStatusCode()) {
                HttpEntity responseEntity = response.getEntity();

                if(responseEntity != null){
                    String s = EntityUtils.toString(responseEntity);
                    if(LOGGER.isTraceEnabled()){
                        LOGGER.trace("Request successful.response,{}",s);
                    }
                    return s;
                }

            } else {
                HttpEntity responseEntity = response.getEntity();
                if(responseEntity != null){
                    String result = EntityUtils.toString(responseEntity);
                    LOGGER.error("Request failed.response:"+result);
                    return result;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("ElasticSearch quest exception",e);
            throw new IndexException(e);
        }



        return null;
    }




}
