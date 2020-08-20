package cn.com.nwdc.db.elem;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author coffee
 * @Classname DbElement
 * @Description TODO
 * @Date 2019/9/7 17:01
 */

@Data
public class DbElement<KEY,VALUE> {


    private String id;

    private String type;

    private String code;

    private String name;

    private Map<KEY,VALUE> properties = new LinkedHashMap<>();

    public void property(KEY key,VALUE value){
        if(properties.containsKey(key)){
            return;
        }

        properties.put(key,value);
    }

    public VALUE property(KEY key){
       return properties.get(key);
    }



}
