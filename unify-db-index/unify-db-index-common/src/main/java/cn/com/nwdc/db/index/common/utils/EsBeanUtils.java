package cn.com.nwdc.db.index.common.utils;

import cn.com.nwdc.db.anno.RootObject;
import cn.com.nwdc.db.anno.cache.TableInfoHelper;
import cn.com.nwdc.db.anno.cache.info.TableFieldInfo;
import cn.com.nwdc.db.anno.cache.info.TableInfo;
import cn.com.nwdc.db.elem.DbElement;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * zhangyr
 * 2019-11-12
 */
public class EsBeanUtils {
    protected static Logger LOGGER = LoggerFactory.getLogger(EsBeanUtils.class);


    public static final String SOURCE  = "_source";


    public static DbElement toDbElement(Object bean) {
        DbElement<String, Object> dbElement = new DbElement();
        Map beanToMap = new LinkedHashMap<>();
        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        TableInfo tableInfo = TableInfoHelper.getTableInfo(bean.getClass());
        Map<String, TableFieldInfo> fieldInfoMap = tableInfo.getFieldList()
                .stream().collect(Collectors.toMap(TableFieldInfo::getProperty, a -> a, (k1, k2) -> k1));
        Arrays.asList(fields).forEach(field -> {
            field.setAccessible(true);
            try {
                if (field.isAnnotationPresent(TableId.class)) {
                    dbElement.setId(String.valueOf(field.get(bean)));
                    beanToMap.put(tableInfo.getKeyColumn(),field.get(bean));
                }else if(field.isAnnotationPresent(TableField.class)){
                    beanToMap.put(fieldInfoMap.get(field.getName()).getColumn(),field.get(bean));
                }else{
                    beanToMap.put(field.getName(), field.get(bean));
                }
            } catch (IllegalAccessException e) {
                LOGGER.error("bean to map error...{}", e.getMessage());
            }
        });
        dbElement.setProperties(beanToMap);
        return dbElement;
    }

    public static int getTotalCount(String resultEntity) {
        JSONObject jsonObject = JSON.parseObject(resultEntity);
        return (int)jsonObject.getJSONObject("hits").get("total");

    }

    public static <ELEM> List<ELEM> loadElemList(JSONArray searchHits, ELEM object, String soureName) {
        List<ELEM> docList = new ArrayList<>();


        for (int i = 0; i < searchHits.size(); i++) {

            Object source = null;
            if(StringUtils.isBlank(soureName)){
                source = searchHits.getJSONObject(i);
            }else{
                source = searchHits.getJSONObject(i).get(soureName);
            }

            JSONObject jsonObject = (JSONObject) JSONObject.toJSON(source);
            if(object instanceof HashMap || object instanceof Map){
                object = (ELEM)jsonObject;
            }else{

                object = (ELEM) JSONObject.toJavaObject(jsonObject, object.getClass());

            }

            docList.add(object);
        }
        return docList;
    }

    public static <ELEM> List<ELEM> loadElemListBySource(JSONArray searchHits, ELEM object) {
       return loadElemList(searchHits,object,SOURCE);
    }

    /**
     * 解析ES返回数据
     * @param resultEntity
     * @param elem
     * @param <ELEM>
     * @return
     */
    public static <ELEM> List<ELEM> parseReponseJson(String resultEntity , ELEM elem, RootObject rootObject) {
        JSONObject jsonObject = JSON.parseObject(resultEntity);
        if(rootObject==null){

            JSONObject aggs = jsonObject.getJSONObject("aggregations");
            JSONArray jsonArray =null;
            boolean isAgg = false;
            if(aggs!=null){
                for(String key:aggs.keySet()){
                    JSONObject group = aggs.getJSONObject(key);
                    isAgg = true;
                    jsonArray = (JSONArray) group.get("buckets");
                    break;
                }
            }else{
                Object hits = jsonObject.get("hits");
                JSONObject data = (JSONObject) JSONObject.toJSON(hits);
                jsonArray = (JSONArray) data.get("hits");

            }
            return loadElemList(jsonArray,elem,isAgg?"":SOURCE);
        }else{


            if(rootObject.value()!=null){
                jsonObject = jsonObject.getJSONObject(rootObject.value());
            }
            if(elem instanceof HashMap || elem instanceof Map){
                elem = (ELEM)jsonObject;
            }else{
                elem = (ELEM) JSONObject.toJavaObject(jsonObject, elem.getClass());
            }
            return Lists.newArrayList(elem);
        }


    }

    public static JSONArray getAggArr(String resultEntity,List<String> groupKeys){
        JSONObject jsonObject = JSON.parseObject(resultEntity);
        JSONObject aggs = jsonObject.getJSONObject("aggregations");
        JSONObject group = aggs.getJSONObject(groupKeys.get(0));
        return (JSONArray) group.get("buckets");
    }

    public static JSONArray getDataArr(String resultEntity) {
        JSONObject jsonObject = JSON.parseObject(resultEntity);
        Object hits = jsonObject.get("hits");
        JSONObject data = (JSONObject) JSONObject.toJSON(hits);
        JSONArray dataArr = (JSONArray) data.get("hits");
        return dataArr;
    }

    /*create by yangheng*/
    public static final String REQUEST_TYPE_DELETE = "delete";
    public static final String REQUEST_TYPE_INSERT = "insert";
    public static final int DEFAULT_QUERY_SIZE = Integer.MAX_VALUE;
    public static final String DEFAULT_TEMPLETE_SEARCH_ALL = "{\n" +
            "  \"query\": {\n" +
            "                \"match_all\": {\n" +
            "                }\n" +
            "  }\n" +
            "}";

    public static String calTimeUtil(long startTime) {
        return (System.currentTimeMillis() - startTime) / 1000 + "s";
    }

    public static String getIndexByClass(Class<?> clazz) {
        return TableInfoHelper.getTableInfo(clazz).getTableName();
    }


}
