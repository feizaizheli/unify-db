package cn.com.nwdc.db.elem;

/**
 * @author coffee
 * @Classname IDbElemFilter
 * @Description TODO
 * @Date 2019/9/16 15:31
 */
public interface IDbElemFilter<ELEM> {

    public ELEM filter(ELEM element);
}
