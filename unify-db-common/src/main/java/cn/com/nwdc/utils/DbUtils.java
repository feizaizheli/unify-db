package cn.com.nwdc.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author coffee
 * @Classname DbUtils
 * @Description TODO
 * @Date 2019/11/5 16:00
 */
public class DbUtils {


    public static void close(ResultSet rs, Statement statement){

        if(null!=rs){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(null!=statement){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static void close(Connection connection,ResultSet rs, Statement statement){

        if(null!=connection){
            try {
                connection.close();
            } catch (SQLException e) {
              //  e.printStackTrace();
            }
        }
        if(null!=rs){
            try {
                rs.close();
            } catch (SQLException e) {
             //   e.printStackTrace();
            }
        }

        if(null!=statement){
            try {
                statement.close();
            } catch (SQLException e) {
            }
        }

    }
}
