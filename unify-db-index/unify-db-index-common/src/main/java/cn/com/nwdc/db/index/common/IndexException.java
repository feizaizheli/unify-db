package cn.com.nwdc.db.index.common;

/**
 * @author coffee
 * @Classname EsException
 * @Description TODO
 * @Date 2020/7/18 16:51
 */
public class IndexException extends RuntimeException{

    public IndexException() {
        super();
    }

    public IndexException(String message) {
        super(message);
    }

    public IndexException(String message, Throwable cause) {
        super(message, cause);
    }

    public IndexException(Throwable cause) {
        super(cause);
    }

    protected IndexException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
