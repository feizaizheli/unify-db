package cn.com.nwdc.db.condition;

import cn.com.nwdc.db.anno.RootObject;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author coffee
 * @Classname DbCondition
 * @Description TODO
 * @Date 2019/10/11 12:58
 */


@Data
public class DbCondition<SQL> {

    protected boolean isTypeAdapter;

    protected SQL sql;

    protected Class<?> elemClass;

    protected String tableName ;

    protected Map<String,Object> params ;

    private RootObject rootObject;

    public SQL getSql() {
        return sql;
    }

    public void setSql(SQL sql) {
        this.sql = sql;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }


    public static DbCondition build(){
        return new DbCondition();
    }


    public DbCondition sql(SQL sql){
        this.sql = sql;
        return this;
    }

    public DbCondition tableName(String tableName){
        this.tableName = tableName;
        return this;
    }


    public DbCondition tableName(Class<?> elemClass){
        this.elemClass = elemClass;
        return this;
    }

    public DbCondition addParam(String key,Object val){
        if(params == null){
            params = new LinkedHashMap<>();
        }
        params.put(key,val);
        return this;
    }


    public DbCondition elemClass(Class<?> elemClass){
        this.elemClass = elemClass;
        return this;
    }


}
