package cn.com.nwdc.db.index.common.utils;


import cn.com.nwdc.db.anno.cache.TableInfoHelper;
import cn.com.nwdc.db.elem.DbElement;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotations.TableId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

/**
 * zhangyr
 * 2019-11-12
 */
public class EsUtils {
    protected static Logger LOGGER = LoggerFactory.getLogger(EsUtils.class);
    public static DbElement objToEsElement(Object bean) {
        DbElement<String, Object> dbElement = new DbElement();
        Map beanToMap = new LinkedHashMap<>();
        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Arrays.asList(fields).forEach(field -> {
            field.setAccessible(true);
            try {
                if (field.isAnnotationPresent(TableId.class)) {
                    dbElement.setId((String) field.get(bean));
                }
                beanToMap.put(field.getName(), field.get(bean));
            } catch (IllegalAccessException e) {
                LOGGER.error("bean to map error...{}", e.getMessage());
            }
        });
        dbElement.setProperties(beanToMap);
        return dbElement;
    }

    public static <ELEM> List<ELEM> loadElemList(JSONArray searchHits, ELEM object) {
        List<ELEM> personDocList = new ArrayList<>();
        for (int i = 0; i < searchHits.size(); i++) {
            Object personSource = searchHits.getJSONObject(i).get("_source");
            JSONObject jsonObject = (JSONObject) JSONObject.toJSON(personSource);
            object = (ELEM) JSONObject.toJavaObject(jsonObject, object.getClass());
            personDocList.add(object);
        }
        return personDocList;
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
