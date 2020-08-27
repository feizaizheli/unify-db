package cn.com.nwdc.unify.db.entity;

import cn.com.nwdc.unify.db.anno.ITableNameStrategy;
import org.springframework.stereotype.Service;

/**
 * @author coffee
 * @Classname UserNameStrategy
 * @Description TODO
 * @Date 2020/7/17 16:07
 */
@Service
public class UserNameStrategy implements ITableNameStrategy<User> {


    private static String prefix = "dev";

    @Override
    public String getTableName(String tableName, User user) {

        int indexId = user.getAge()%5;
        return prefix +"_"+tableName+"_"+indexId;
    }
}
