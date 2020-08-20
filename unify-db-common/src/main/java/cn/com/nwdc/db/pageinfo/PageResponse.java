package cn.com.nwdc.db.pageinfo;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * @author coffee
 * @Classname PageResponse
 * @Description TODO
 * @Date 2019/9/25 19:19
 */
@Data
public class PageResponse<DATA> extends ApiResponse {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageResponse.class);

    public static final String SUCCESS_CODE = "200";

    public static final String SUCCESS_MSG = "success";


    public static final String MARC_GROUP_PAGE_INFO = "#groupPageInfo";


    public static <DATA> PageResponse pageInfo(String code,List<DATA> datas, PageInfo pageInfo){
        if(pageInfo == null){
            pageInfo = new PageInfo();
        }

        if(pageInfo.getGroups()!=null && pageInfo.getGroups().containsKey(MARC_GROUP_PAGE_INFO)){
            pageInfo.getGroups().remove(MARC_GROUP_PAGE_INFO);
        }
        if(datas!=null){
            pageInfo.setDataList(datas);
        }

        PageResponse pageResponse = new PageResponse();
        pageResponse.setCode(code+SUCCESS_CODE);
        pageResponse.setMessage(SUCCESS_MSG);
        pageResponse.setBody(pageInfo);
        return pageResponse;
    }

    public static <DATA> PageResponse pageInfo(List<DATA> datas, PageInfo pageInfo){
        return pageInfo("",datas,pageInfo);
    }


 /*   public static <DATA> PageResponse groupPageInfo(String code,List<DATA> dataList, PageInfo groupPageInfo){


        return groupPageInfo(code,null,groupPageInfo,null,null);
    }

    public static <DATA> PageResponse groupPageInfo(String code,List<DATA> dataList, Map<String,Long> groupCount){
        return groupPageInfo(code,null,groupPageInfo,null,null);
    }*/



    public static <DATA> PageResponse groupPageInfo( List<DATA> dataList, PageInfo pageInfo,
                                                     IGroupPageFilter<Map<String,Long>>... groupPageFilters){


        if(dataList == null || dataList.isEmpty()){
            return pageInfo("",null,pageInfo);
        }

        PageInfo groupPage = pageInfo.getGroupPage(MARC_GROUP_PAGE_INFO);

        DATA firstData = dataList.get(0);
        String groupKey = pageInfo.getGroupKey();
        if(firstData instanceof Map){
            List<Map> datas = (List<Map>)dataList;
            for(Map map:datas){
                pageInfo.putGroup(map.get(groupKey).toString(),map,groupPage);
            }
        }else{
            try {
                Class<?> clazz = dataList.get(0).getClass();
                Field field = clazz.getDeclaredField(groupKey);
                field.setAccessible(true);
                for(DATA data:dataList){
                    pageInfo.putGroup(field.get(data).toString(),data,groupPage);
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                LOGGER.error("Field[]获取异常",groupKey,e);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                LOGGER.error("Field[]获取异常",groupKey,e);
            }
        }

      /*  if(null != groupCount){
            Map<String,PageInfo> groups = pageInfo.getGroups();
            for(Map.Entry<String,PageInfo> entry:groups.entrySet()){
                entry.getValue().setTotalCount(groupCount.get(entry.getKey()));
            }
        }*/

        if(!pageInfo.getGroups().isEmpty()){
            pageInfo.getGroups().remove(MARC_GROUP_PAGE_INFO);
        }
        if(groupPageFilters!=null){
            for(IGroupPageFilter<Map<String,Long>> groupPageFilter:groupPageFilters){
                Map<String,PageInfo> groups = pageInfo.getGroups();
                Map<String,Long> groupCount = groupPageFilter.filter(groups.keySet());
                for(Map.Entry<String,PageInfo> entry:groups.entrySet()){
                    if(groupCount.containsKey(entry.getKey())) {
                        entry.getValue().setTotalCount(groupCount.get(entry.getKey()));
                    }
                }
            }
        }

        return pageInfo("",null,pageInfo);
    }


}
