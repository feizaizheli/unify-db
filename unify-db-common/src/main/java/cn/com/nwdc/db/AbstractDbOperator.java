package cn.com.nwdc.db;


import cn.com.nwdc.db.condition.DbCondition;
import cn.com.nwdc.db.elem.IDbElemFilter;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author coffee
 * @Classname AbstractDbOperator
 * @Description TODO
 * @Date 2019/9/15 17:42
 */
public abstract class AbstractDbOperator<ELEM ,
        CONDITION extends DbCondition> implements IDbOperator<ELEM, CONDITION> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDbOperator.class);

    public static final int DEFAULT_FETCH_SIZE = 50;


    protected int fetchSize = DEFAULT_FETCH_SIZE;



    @Override
    public void batchSave(List<ELEM> sources, IDbElemFilter<ELEM>... elemFilters) {
        if(sources!=null && !sources.isEmpty()){
            batchSave(sources.iterator(),elemFilters);
        }
    }


    @Override
    public void batchSaveRepairDuplicate(List<ELEM> elems, boolean isDuplicateRetryIterSingle) {

    }


    @Override
    public int saveOrUpdate(List<ELEM> elems) {
        return update(elems);
    }

    @Override
    public int saveOrUpdate(ELEM elem, Class<?> elemClass) {
        return saveOrUpdate(elem,elemClass);
    }

    @Override
    public void batchSave(Iterator<ELEM> elemIterator, IDbElemFilter<ELEM>... elemFilters) {
        List<ELEM> tmpElemList = new ArrayList<>(fetchSize);
        long startTime = System.currentTimeMillis();
        long batchStartTime = System.currentTimeMillis();
        int index = 0;
        if(elemIterator !=null){
            while (elemIterator.hasNext()){
                index++;
                ELEM element = elemIterator.next();
                if(elemFilters !=null && elemFilters.length>0){
                    for(IDbElemFilter elementConvertor: elemFilters){
                        elementConvertor.filter(element);
                    }
                }
                tmpElemList.add(element);
                if(fetchSize == tmpElemList.size()){
                    startTime = System.currentTimeMillis();
                    doBatchSave(tmpElemList);
                    tmpElemList.clear();
                    if(LOGGER.isTraceEnabled()){
                        long costTime = System.currentTimeMillis()-batchStartTime;
                        LOGGER.trace("Batch data submit successfully Current batch number is [{}] Time-consuming [{}]ms,Current speed[{}] number/s, total number[{}],average speed[{}] number/s",
                                fetchSize,costTime,(fetchSize*1000)/costTime,index,(index*1000)/System.currentTimeMillis()-startTime);
                    }
                    batchStartTime = System.currentTimeMillis();
                }

            }
            if(tmpElemList.size()>0){
                doBatchSave(tmpElemList);
                if(LOGGER.isDebugEnabled()){
                    long cost = System.currentTimeMillis()-startTime;
                    LOGGER.debug("Last batch of data submitted total[{}] Time-consuming [{}] second average speed[{}] number/s",index,(cost/1000)==0?1:(cost/1000),(index*1000)/cost);
            }
            }
        }

    }



    @Override
    public Iterator<Map<String, Object>> iteratorMap(CONDITION condition) {
        return null;
    }

    protected abstract void doBatchSave(List<ELEM> elemList);


    public int getFetchSize() {
        return fetchSize;
    }

    @Override
    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }




}
