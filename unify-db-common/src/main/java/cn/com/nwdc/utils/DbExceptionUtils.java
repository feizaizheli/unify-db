package cn.com.nwdc.utils;

import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.BatchUpdateException;

/**
 * @author coffee
 * @Classname DbExceptionUtils
 * @Description TODO
 * @Date 2020/2/26 18:10
 */
public class DbExceptionUtils {


    public static String SQL_DUPLICATE_FLAG = "duplicate key";

    private static final Logger LOGGER = LoggerFactory.getLogger(DbExceptionUtils.class);

    public static boolean isDuplicateByBatchExecute(Exception e,boolean isLog){



        if(e.getMessage().toLowerCase().contains(SQL_DUPLICATE_FLAG)){
            //LOGGER.warn("[duplicate key]："+e.getMessage());
            if(isLog){
                LOGGER.warn("",e.getMessage());
            }

            return true;
        }
        if( null != e.getCause() && e.getCause() instanceof BatchUpdateException){
            return isBatchUpdateException(e.getCause(),isLog);

        }else if(null != e.getCause() && e.getCause() instanceof PersistenceException){
            if(e.getCause()!=null && e.getCause().getCause()!=null){
                return isBatchUpdateException(e.getCause().getCause(),isLog);
            }

          /*  if(null != b.getNextException()){
                String msg = b.getNextException().getMessage();
                if(msg.toLowerCase().contains(SQL_DUPLICATE_FLAG)){
                    //   LOGGER.warn("batch insert duplicate key ");
                    return true;
                }else{
                    LOGGER.error("no dupulicate Key exception,exception is ",b.getNextException());
                }
            }*/
        }


        return false;
    }

    private static boolean isBatchUpdateException(Throwable e,boolean isLog) {
        BatchUpdateException b = (BatchUpdateException)e;
        if(null != b.getNextException()){
            String msg = b.getNextException().getMessage();
            if(msg.toLowerCase().contains(SQL_DUPLICATE_FLAG)){
             //   LOGGER.warn("batch insert duplicate key ");
                if(isLog){
                    LOGGER.warn("[duplicate key]："+msg);
                }

                return true;
            }else{
                if(isLog){
                    LOGGER.error("db exception is ",b.getNextException());
                }

            }
        }
        return false;
    }
}
