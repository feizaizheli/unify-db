package cn.com.nwdc.db;

import cn.com.nwdc.db.anno.cache.TableInfoHelper;
import cn.com.nwdc.db.anno.cache.info.TableInfo;
import cn.com.nwdc.db.condition.DbCondition;
import cn.com.nwdc.utils.ClassUtil;
import cn.com.nwdc.utils.DbExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author coffee
 * @Classname AbstractRdbOperator
 * @Description TODO
 * @Date 2019/10/30 16:05
 */
public abstract class AbstractRdbOperator<ELEM> extends AbstractDbOperator<ELEM , DbCondition<String>> implements IRdbOperator<ELEM>{


    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRdbOperator.class);
    @Autowired
    protected DataSource dataSource;


    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public void batchSaveRepairDuplicate(List<ELEM> elemList, boolean isIgnoreDuplicate) {
        try {
            batchSave(elemList);

        } catch (Exception batchSaveEx) {
            if(isIgnoreDuplicate && DbExceptionUtils.isDuplicateByBatchExecute(batchSaveEx,false)){
                LOGGER.warn("【duplicate repair】recognize batch insert error,start repair");
                int repairSuccessIndex = 0;
                TableInfo tableInfo = TableInfoHelper.getTableInfo(elemList.get(0).getClass());

                for (ELEM elem:elemList){
                    try {
                        LOGGER.warn("【duplicate repair】 key[{}] ", ClassUtil.getFieldValue(tableInfo.getKeyProperty(),elem));
                        save(elem);
                        LOGGER.warn("【duplicate repair】 key[{}] success ",ClassUtil.getFieldValue(tableInfo.getKeyProperty(),elem));
                        repairSuccessIndex++;
                    } catch (Exception saveEx) {
                        if(!DbExceptionUtils.isDuplicateByBatchExecute(saveEx,false)){
                            throw saveEx;
                        }
                    }

                }
                LOGGER.info("【duplicate repair】 finish,success[{}],totalCount[{}]",repairSuccessIndex,elemList.size());

            }else{
                LOGGER.error("",batchSaveEx);
                throw batchSaveEx;
            }
        }
    }



}
