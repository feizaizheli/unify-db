package cn.com.nwdc.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author coffee
 * @Classname MapUtils
 * @Description TODO
 * @Date 2020/7/20 15:20
 */
public class MapUtils {


    public static void sort(List<Map<String,Object>> dataList,String keyName){

        Collections.sort(dataList, new MapComparatorDesc(keyName));

    }

    public static class MapComparatorDesc implements Comparator<Map<String, Object>> {

        private String keyName;

        public MapComparatorDesc(String keyName) {
            this.keyName = keyName;
        }

        @Override
        public int compare(Map<String, Object> m1, Map<String, Object> m2) {
            String v1 = String.valueOf(m1.get(keyName).toString());
            String v2 = String.valueOf(m2.get(keyName).toString());
            if (v2 != null) {
                return v2.compareTo(v1);
            }
            return 0;
        }

    }
}
