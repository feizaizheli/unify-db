package cn.com.nwdc.db.rdb.mybatis;

import cn.com.nwdc.db.AbstractRdbOperator;
import cn.com.nwdc.db.condition.DbCondition;
import cn.com.nwdc.db.pageinfo.PageInfo;
import cn.com.nwdc.db.rdb.common.RdbDbIterator;
import cn.com.nwdc.db.rdb.common.sql.ISQLBuilder;
import cn.com.nwdc.db.rdb.common.sql.SQL;
import cn.com.nwdc.db.rdb.common.utils.BeanUtils;
import cn.com.nwdc.utils.ClassUtil;
import cn.com.nwdc.utils.DbExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author coffee
 * @Classname MybatisRdbOperator
 * @Description TODO
 * @Date 2020/3/17 9:33
 */

@Component("mybatisRdbOperator")
public class MybatisRdbOperator<ELEM> extends AbstractRdbOperator<ELEM> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisRdbOperator.class);

    @Autowired
    private SqlSessionFactory sqlSessionFactory;


    @PostConstruct
    public void init() {
       sqlSessionFactory.getConfiguration().addMapper(SqlMapper.class);

    }


    @Override
    public Iterator<Map<String, Object>> iteratorMap(DbCondition<String> condition) {

        SqlSession sqlSession = null;

        sqlSession = sqlSessionFactory.openSession();
        Connection connection = sqlSession.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("SQL:[{}] idName:[{}]", condition.getSql(), condition.getElemClass());
            }
            connection.setAutoCommit(false);
            ps = connection.prepareStatement(condition.getSql());
            ps.setFetchSize(fetchSize); //每次获取1万条记录
            rs = ps.executeQuery();

            return new RdbDbIterator(rs, condition.getElemClass());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
                /*try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }*/
                /*if(sqlSession != null){
                    sqlSession.close();
                }*/
        }


    }

    @Override
    public PageInfo<ELEM> queryForPage(DbCondition<String> condition, PageInfo<ELEM> pageInfo) {
        List<ELEM> elemList = queryForList(condition);
        pageInfo.setDataList(elemList);
        //toDo
        pageInfo.setTotalCount(1);
        return pageInfo;
    }

    @Override
    public PageInfo<Map<String, Object>> queryForMapPage(DbCondition<String> condition, PageInfo<Map<String,Object>> pageInfo) {
        List<Map<String,Object>> elemList = queryForMapList(condition);
        pageInfo.setDataList(elemList);
        //toDo
        pageInfo.setTotalCount(1);
        return pageInfo;
    }

    @Override
    public Object rawExecute(DbCondition<String> condition) {
        return null;
    }

    @Override
    protected void doBatchSave(List<ELEM> elemList) {
        SqlSession sqlSession = null;
        try {
            sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
            SqlMapper sqlMapper = sqlSession.getMapper(SqlMapper.class);
            SQL.INSERT<ELEM> insert = SQL.INSERT.build(elemList.get(0));
            for (ELEM elem : elemList) {
                sqlMapper.insert(
                        insert.getTableInfo().getTableName(),
                        insert.keyFieldSQL(),
                        insert.valFieldSQL(SQL.SQL_PARAMS, SQL.SQL_PAMAMS_END), elem);
            }
            sqlSession.commit();
        } catch (Exception e) {
            sqlSession.rollback();
            DbExceptionUtils.isDuplicateByBatchExecute(e, true);
            throw e;

        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }


    @Override
    public void createTable(String tableName) {

    }

    @Override
    public void deleteTable(String tableName) {
        SqlSession sqlSession = null;
        try {
            sqlSession = sqlSessionFactory.openSession();
            SqlMapper sqlMapper = sqlSession.getMapper(SqlMapper.class);
            sqlMapper.deleteTable(tableName);
            sqlSession.commit();
        } catch (Exception e) {
            sqlSession.rollback();
            DbExceptionUtils.isDuplicateByBatchExecute(e, true);
            throw e;

        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }

    }

    @Override
    public void truncateTable(String tableName) {
        String sql = "truncate table " + tableName;

    }

    @Override
    public boolean isExistTable(String tableName) {
        return false;
    }



    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(ELEM elem) {

        SqlSession sqlSession = null;
        try {
            sqlSession = sqlSessionFactory.openSession();
            SqlMapper sqlMapper = sqlSession.getMapper(SqlMapper.class);
            ISQLBuilder insert = SQL.SQLFactory.build(SQL.Type.INSERT, elem);
            sqlMapper.insert(
                    insert.getTableInfo().getTableName(),
                    insert.keyFieldSQL(),
                    insert.valFieldSQL(SQL.SQL_PARAMS, SQL.SQL_PAMAMS_END),
                    elem
            );

            sqlSession.commit();
            sqlSession.close();
        } catch (Exception e) {
            e.printStackTrace();
            sqlSession.rollback();
            DbExceptionUtils.isDuplicateByBatchExecute(e, true);
            throw e;

        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }





    @Override
    public void delete(Serializable id, Class<?> elemClass) {
        SqlSession sqlSession = null;
        try {
            sqlSession = sqlSessionFactory.openSession();
            SqlMapper sqlMapper = sqlSession.getMapper(SqlMapper.class);
            SQL.DELETE<ELEM> delete = SQL.DELETE.build(elemClass.newInstance());
            sqlMapper.delete(
                    delete.getTableInfo().getTableName(),
                    delete.keyFieldSQL(),
                    id);

            sqlSession.commit();
        } catch (Exception e) {
            sqlSession.rollback();
            DbExceptionUtils.isDuplicateByBatchExecute(e, true);
            try {
                throw e;
            } catch (InstantiationException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }

        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }

    }

    @Override
    public void delete(List<Serializable> ids, Class<?> elemClass) {
        SqlSession sqlSession = null;
        try {
            sqlSession = sqlSessionFactory.openSession();
            SqlMapper sqlMapper = sqlSession.getMapper(SqlMapper.class);
            SQL.DELETE<ELEM> delete = SQL.DELETE.build(elemClass.newInstance());
            sqlMapper.delete(
                    delete.getTableInfo().getTableName(),
                    delete.keyFieldSQL(),
                    "'" + StringUtils.join(ids.toArray(), "','") + "'");
            sqlSession.commit();
        } catch (Exception e) {
            sqlSession.rollback();
            DbExceptionUtils.isDuplicateByBatchExecute(e, true);
            try {
                throw e;
            } catch (InstantiationException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }

        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }


    }

    @Override
    public void update(List<ELEM> elems) {
        SqlSession sqlSession = null;
        try {
            sqlSession = sqlSessionFactory.openSession();
            SqlMapper sqlMapper = sqlSession.getMapper(SqlMapper.class);
            SQL.UPDATE<ELEM> update = SQL.UPDATE.build(elems.get(0));
            for (ELEM elem : elems) {
                sqlMapper.update(
                        update.getTableInfo().getTableName(),
                        update.valFieldSQL("", ClassUtil.getFieldValue(update.getTableInfo().getKeyProperty(), elem).toString()), elem);
            }
            sqlSession.commit();
        } catch (Exception e) {
            sqlSession.rollback();
            DbExceptionUtils.isDuplicateByBatchExecute(e, true);
            throw e;

        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }

    }

    @Override
    public void update(ELEM elem, Class<?> elemClass) {
        SqlSession sqlSession = null;
        try {
            sqlSession = sqlSessionFactory.openSession();
            SqlMapper sqlMapper = sqlSession.getMapper(SqlMapper.class);
            SQL.UPDATE<ELEM> update = SQL.UPDATE.build(elem);
            sqlMapper.update(
                    update.getTableInfo().getTableName(),
                    update.valFieldSQL("", ClassUtil.getFieldValue(update.getTableInfo().getKeyProperty(), elem).toString()), elem);

            sqlSession.commit();
        } catch (Exception e) {
            sqlSession.rollback();
            DbExceptionUtils.isDuplicateByBatchExecute(e, true);
            throw e;

        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }



    @Override
    public ELEM findElemById(Serializable id, Class<?> elemClass) {

        return null;
    }





    @Override
    public ELEM findElemByCondition(DbCondition<String> condition, Class<?> elemClass) {
        SqlSession sqlSession = null;
        try {
            sqlSession = sqlSessionFactory.openSession();
            SqlMapper sqlMapper = sqlSession.getMapper(SqlMapper.class);
            Map<String, Object> elemMap = sqlMapper.selectElem(condition.getSql());
            if (elemMap == null) {
                return null;
            }
            return BeanUtils.mapToObject(elemMap, condition.getElemClass(),condition.isTypeAdapter());
        } catch (Exception e) {
            sqlSession.rollback();
            throw e;

        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }




    @Override
    public List<Map<String, Object>> queryForMapList(DbCondition<String> condition) {
        SqlSession sqlSession = null;
        try {
            sqlSession = sqlSessionFactory.openSession();
            SqlMapper sqlMapper = sqlSession.getMapper(SqlMapper.class);
            return sqlMapper.selectList(condition.getSql());
        } catch (Exception e) {
            sqlSession.rollback();

            throw e;

        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }



    @Override
    public List<ELEM> queryForList(DbCondition<String> condition) {

        List<Map<String,Object>> elemList = queryForMapList(condition);
        List<ELEM> resultList = new ArrayList<>();
        if (elemList.size() > 0 || elemList != null) {
            elemList.forEach(obj -> {
                resultList.add(BeanUtils.mapToObject(obj,condition.getElemClass()));
            });
        }
        return resultList;
    }



    @Override
    public Iterator<ELEM> iterator(DbCondition<String> condition) {
        SqlSession sqlSession = null;
        try {
            sqlSession = sqlSessionFactory.openSession();
            SqlMapper sqlMapper = sqlSession.getMapper(SqlMapper.class);
            Cursor<ELEM> cursor = sqlMapper.selectCursor(condition.getSql());
            Iterator<ELEM> iterator = cursor.iterator();
            return iterator;
        } catch (Exception e) {
            sqlSession.rollback();
            throw e;

        } finally {
            if (sqlSession != null) {
                // sqlSession.close();
            }
        }

    }


    @Override
    public void close() throws IOException {

    }
}
