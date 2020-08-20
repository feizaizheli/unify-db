package cn.com.nwdc.db.index.common;

import cn.com.nwdc.db.condition.DbCondition;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author coffee
 * @Classname ESDbCondition
 * @Description TODO
 * @Date 2020/7/17 12:25
 */

@Builder
@Data
public class IndexDbCondition extends DbCondition<String> {



    private boolean isTemplate;

    private SQLTemplate sqlTemplate;

    private String extendUrl;

    private List<String> groupKeys;




    public IndexDbCondition template(String templateName, String templateMethod){
        sqlTemplate = new SQLTemplate(templateName,templateMethod);
        return this;
    }


    public void addGroupKey(String groupKey){
        if(groupKeys == null){
            groupKeys = Lists.newLinkedList();
        }
        groupKeys.add(groupKey);
    }
    @Override
    public String getSql() {
        if(sqlTemplate != null){
            return IndexDbTemplate.SQL(sqlTemplate.getTemplateName(),sqlTemplate.getTemplateMethod(),params);
        }
        return sql;
    }





    @Builder
    @Data
    @AllArgsConstructor
    public static class SQLTemplate{

        private String templateName;

        private String templateMethod;


    }

}
