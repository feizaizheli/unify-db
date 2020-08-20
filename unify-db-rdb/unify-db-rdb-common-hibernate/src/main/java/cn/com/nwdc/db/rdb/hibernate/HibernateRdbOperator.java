package cn.com.nwdc.db.rdb.hibernate;

import cn.com.nwdc.db.AbstractRdbOperator;
import cn.com.nwdc.db.condition.DbCondition;
import cn.com.nwdc.db.pageinfo.PageInfo;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Pattern;

import static org.hibernate.transform.Transformers.ALIAS_TO_ENTITY_MAP;

/**
 * @author coffee
 * @Classname HibernateRdbOperator
 * @Description TODO
 * @Date 2020/4/27 14:04
 */
@Service

public class HibernateRdbOperator<ELEM> extends AbstractRdbOperator<ELEM> {


    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateRdbOperator.class);

    /**
     *  ora: SELECT TO_CHAR(SYSDATE,'YYYYMMDD') SYS_TIME FROM DUAL
     *mysql: select date_format(sysdate(),'%Y-%m-%d') as SYS_TIME FROM DUAL
     */
    private String sqlSysData;
    /**
     *  ora: SELECT TO_CHAR(SYSDATE,'YYYYMMDDHH24MiSS') SYS_TIME FROM DUAL
     *mysql: select date_format(sysdate(),'%Y-%m-%d %H:%i:%s') as SYS_DATE FROM DUAL
     */
    private String sqlSysTime;
    /**
     * MACRO_END
     */
    private static final String MACRO_END = "}";

    /**
     * MACRO_BEGIN
     */
    private static final String MACRO_BEGIN = "${";

    /**
     * PARAM_SQL_END
     */
    private static final String PARAM_SQL_END = "~/";

    /**
     * PARAM_SQL_BEGIN
     */
    private static final String PARAM_SQL_BEGIN = "/~";

    /**
     * REGX_SQL_PARAM
     */
    private static final String REGX_SQL_PARAM = ".*:([\\w\\d_]+).*";

    /**
     * MACRO_PATTERN
     */
    private static final Pattern MACRO_PATTERN = Pattern.compile("\\$\\{\\s*(\\w+)\\s*\\}");


    @Autowired
    private SessionFactory sessionFactory;



    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public Session openSession() {
        return sessionFactory.openSession();
    }


    @Override
    protected void doBatchSave(List<ELEM> elems) {
        Session session = getSession();
        if (elems != null) {
            Iterator it = elems.iterator();
            while (it.hasNext()) {
                session.save(it.next());
            }
        }

    }



    @Override
    public void createTable(String tableName) {

    }

    @Override
    public void deleteTable(String tableName) {

    }

    @Override
    public void truncateTable(String tableName) {

    }

    @Override
    public boolean isExistTable(String tableName) {
        return false;
    }


    @Override
    public int save(ELEM elem) {
        Session session = getSession();
        session.save(elem);
        return 1;
    }

    @Override
    public int delete(Serializable id, Class<?> elemClass) {


        Object obj = this.get(elemClass,id);
        this.delete(obj);
        return 0;
    }

    public Object get(Class refClass, Serializable key) {
        Session s = getSession();
        return s.get(refClass, key);
    }

    @Override
    public int delete(List<Serializable> ids, Class<?> elemClass) {
        if (ids != null) {
            for (int i = 0; i < ids.size(); i++) {
                Object obj = this.get(elemClass, ids.get(i));
                this.delete(obj);
            }
        }
        return 0;
    }

    public void delete(final Object obj) {
        Session session = getSession();
        session.delete(obj);
    }


    @Override
    public int update(List<ELEM> elems) {
        Session session = getSession();
        if (elems != null) {
            Iterator it = elems.iterator();
            while (it.hasNext()) {
                session.update(it.next());
            }
        }
        return 0;
    }

    @Override
    public int update(ELEM elem, Class<?> elemClass) {
        Session session = getSession();
        session.update(elem);
        return 0;
    }

    @Override
    public void saveOrUpdate(List<ELEM> elems) {


        Session session = getSession();
        for(ELEM elem:elems){
            session.saveOrUpdate(elem);
        }


    }

    @Override
    public void saveOrUpdate(ELEM elem, Class<?> elemClass) {
        Session session = getSession();
        session.saveOrUpdate(elem);
    }



    @Override
    public ELEM findElemById(Serializable id, Class<?> elemClass) {
        Session s = getSession();
        return (ELEM)s.get(elemClass, id);
    }

    @Override
    public ELEM findElemByCondition(DbCondition<String> condition, Class<?> elemClass) {
        Object o = null;
        try {
            // Query.uniqueResult()
            List list = this.getQuery(condition.getSql(), condition.getParams()).list();
            if (list.size() > 0) {
                o = list.get(0);
            }
        } catch (HibernateException e) {
            LOGGER.error(condition.getSql());
            e.printStackTrace();
            throw e;
        }
        return (ELEM)o;
    }






    @Override
    public List<ELEM> queryForList(DbCondition<String> condition) {
        return queryForList(condition);

    }



    @Override
    public List<Map<String,Object>> queryForMapList(DbCondition<String> dbCondition){
        try {
            Query query = this.getQuery(dbCondition.getSql(), dbCondition.getParams());
            return query.list();
        } catch (HibernateException e) {
            LOGGER.error(dbCondition.getSql());
            e.printStackTrace();
            throw e;
        }
    }




    /**
     * 加强版的getSQLQuery,可以处理in(:paramName)格式的参数，
     * 此时参数用实现iterator接口的类型传入，如List;也可以用数组传入，如String[]
     *
     * @param sql
     *            查询sql
     * @param params
     *            查询参数Map
     * @return SQLQuery
     * @author likf
     *
     */
    protected SQLQuery getSqlQuery(Session session, String sql, Map params) {
        LOGGER.debug("getSqlQuery:"+sql);
        //String queryStr = sqlParse(new StringBuffer(sql), params);
        String queryStr = sqlParse(new StringBuffer(sql),params);
        SQLQuery q = null;// = getQuery(queryStr, s);
        if (null != params) {
            Map newParams = new HashMap();
            newParams.putAll(params);
            for (Iterator i = params.entrySet().iterator(); i.hasNext();) {
                Map.Entry entry = (Map.Entry) i.next();
                /**
                 * Iterable不能用于JDK 1.4
                 */
                Object value = entry.getValue();
                if (value instanceof Iterator || value instanceof Collection) {
                    StringBuffer sb = new StringBuffer();
                    String paramName = (String) entry.getKey();
                    Iterator it = null;
                    if (value instanceof Iterator) {
                        it = (Iterator) value;
                    } else {
                        it = ((Collection) value).iterator();
                    }
                    int idx = 0;
                    if (it.hasNext()) {
                        while (it.hasNext()) {
                            if (idx > 0) {
                                sb.append(",");
                            }
                            sb.append(":").append(paramName).append("_").append(idx);
                            newParams.put(paramName + "_" + idx, it.next());
                            idx++;
                        }
                        queryStr = queryStr.replaceAll(":" + paramName, sb.toString());
                        newParams.remove(paramName);
                    } else {
                        newParams.put(entry.getKey(), null);
                    }

                } else if (entry.getValue() != null && entry.getValue().getClass().isArray()) {
                    StringBuffer sb = new StringBuffer();
                    String paramName = (String) entry.getKey();
                    int length = Array.getLength(entry.getValue());
                    for (int j = 0; j < length; j++) {
                        Object paramItem = Array.get(entry.getValue(), j);
                        if (j > 0) {
                            sb.append(",");
                        }
                        sb.append(":").append(paramName).append("_").append(j);
                        newParams.put(paramName + "_" + j, paramItem);
                    }
                    queryStr = queryStr.replaceAll(":" + paramName, sb.toString());
                    newParams.remove(paramName);
                }
            }
            q = session.createSQLQuery(queryStr);
            for (Iterator i = newParams.entrySet().iterator(); i.hasNext();) {
                Map.Entry entry = (Map.Entry) i.next();
                q.setParameter((String) entry.getKey(), entry.getValue());
            }
        } else {
            q = session.createSQLQuery(queryStr);
        }
        return q;
    }

    /**
     * create a query. 通过hql创建Query对象
     *
     * @param hql
     *            a query expressed in Hibernate's query language
     * @return Query
     */
    public Query getQuery(String hql) {
        Session s = getSession();
        return s.createQuery(hql);
    }

    public Query getQuery(String hql, Map params) {
        LOGGER.debug(hql);
        String hqlStr = sqlParse(new StringBuffer(hql), params);
        Query q = null;// = getQuery(queryStr, s);
        if (null != params) {
            Map newParams = new HashMap();
            newParams.putAll(params);
            for (Iterator i = params.entrySet().iterator(); i.hasNext();) {
                Map.Entry entry = (Map.Entry) i.next();
                Object value = entry.getValue();
                /**
                 * 不兼容1.4 if (entry.getValue() instanceof Iterable) {
                 */
                if (value instanceof Iterator || value instanceof Collection) {
                    StringBuffer sb = new StringBuffer();
                    String paramName = (String) entry.getKey();
                    // Iterator it = (( Iterable ) entry.getValue()).Iterator();

                    Iterator it = null;
                    if (value instanceof Iterator) {
                        it = (Iterator) value;
                    } else {
                        it = ((Collection) value).iterator();
                    }
                    int idx = 0;
                    if (it.hasNext()) {
                        while (it.hasNext()) {
                            if (idx > 0) {
                                sb.append(",");
                            }
                            sb.append(":").append(paramName).append("_").append(idx);
                            newParams.put(paramName + "_" + idx, it.next());
                            idx++;
                        }
                        hqlStr = hqlStr.replaceAll(":" + paramName, sb.toString());
                        newParams.remove(paramName);
                    } else {
                        newParams.put(entry.getKey(), null);
                    }
                } else if (entry.getValue() != null && entry.getValue().getClass().isArray()) {
                    StringBuffer sb = new StringBuffer();
                    String paramName = (String) entry.getKey();
                    int length = Array.getLength(entry.getValue());
                    for (int j = 0; j < length; j++) {
                        Object paramItem = Array.get(entry.getValue(), j);
                        if (j > 0) {
                            sb.append(",");
                        }
                        sb.append(":").append(paramName).append("_").append(j);
                        newParams.put(paramName + "_" + j, paramItem);
                    }
                    hqlStr = hqlStr.replaceAll(":" + paramName, sb.toString());
                    newParams.remove(paramName);
                }
            }
            LOGGER.info(hqlStr);
            q = getQuery(hqlStr);
            for (Iterator i = newParams.entrySet().iterator(); i.hasNext();) {
                Map.Entry entry = (Map.Entry) i.next();
                q.setParameter((String) entry.getKey(), entry.getValue());
            }
        } else {
            q = getQuery(hqlStr);
        }
        return q;
    }



    /**
     * sqlParse SQL语法解析，包含宏替换和条件语法
     *
     * @param sql
     *            原始SQL
     * @param params
     *            参数
     * @return 解析后的最终SQL
     * @author likf
     */
    private String sqlParse(StringBuffer sql, Map params) {
        replaceMacro(sql, params);
        paramSqlParse(sql, params);
        return sql.toString();
    }

    /**
     * paramSqlParse 参数SQL语法解析 /~ and A.COLUMN_NAME = :PARAM_NAME
     * ~/类似的表达式将根据其中的参数是否在传入条件中存在进行解析 不存在则该段SQL不执行，存在则执行
     *
     * @param sql
     *            原sql，包含语法语法块
     * @param params
     *            执行参数
     * @author likf
     */
    private void paramSqlParse(StringBuffer sql, Map params) {
        int k = sql.indexOf(PARAM_SQL_BEGIN);
        if (k < 0) {
            return;
        }
        int j = 0;
        while (k > 0) {
            j = sql.indexOf(PARAM_SQL_END, k + 2);
            int m = sql.indexOf(PARAM_SQL_BEGIN, k + 2);
            if (j < 0) {
                throw new IllegalArgumentException("SQL语法错误，缺少~/，位置：" + k + "\n" + sql.substring(k));
            }
            if (m > 0 && m < j) {
                throw new IllegalArgumentException("SQL语法错误，【/~】和【~/】不匹配，位置：" + k + "\n" + sql.substring(k, j + 2));
            }
            // 采用位置计算参数看速度是否有更快
            int n = sql.indexOf(":", k + 2);
            if (n > j) {
                throw new IllegalArgumentException("SQL语法错误，/~ ~/之间必须含有“:paramName”形式的参数表达式，位置" + k + "\n"
                        + sql.substring(k + 2, j));
            } else {
                // TODO 参数名后面没有空格会出错
                // int l = sql.indexOf(" ", n);
                int l = n + 2;
                for (; l < j; l++) {
                    int ch = sql.charAt(l);
                    if (!((ch >= 65 && ch <= 90) || (ch >= 97 && ch <= 122) || ch == 95)) {
                        break;
                    }
                }

                if (l > j || l < 0) {
                    l = j;
                }
                String pName = sql.substring(n + 1, l);
                // 判断条件中是否包含了参数，且不为空
                if (params.containsKey(pName) && params.get(pName) != null
                        && !"".equals(params.get(pName).toString())) {
                    sql.delete(j, j + 2);
                    sql.delete(k, k + 2);
                    k = k - 2;
                    j = j - 4;
                } else {// 不包含,删除语法块及参数
                    sql.delete(k, j + 2);
                    params.remove(pName);
                    j = k;
                }
            }
            /**
             * 使用上面的算法替换，提高性能 String str = sql.substring(k + 2, j); // 解析其中的关联参数
             * if (str.matches(REGX_SQL_PARAM)) { String pName =
             * str.replaceAll(REGX_SQL_PARAM, "$1"); // 判断条件中是否包含了参数 if
             * (params.containsKey(pName)) { sql.delete(j, j + 2); sql.delete(k,
             * k + 2); k = k - 2; j = j - 4; } else {// 不包含,删除语法块 sql.delete(k,
             * j + 2); j = k; } } else { throw new
             * IllegalArgumentException("SQL语法错误，/~
             * ~/之间必须含有“:paramName”形式的参数表达式，位置"+k+"\n" + str); }
             */
            k = sql.indexOf(PARAM_SQL_BEGIN, k);
        }
    }


    /**
     * getSessionFactory
     *
     * @return sessionFactory
     */

    /**
     * 替换SQL语句中的宏参数 宏用${macro_name}形式表示 replaceMacro 2010-6-23
     * 增加对动态SQL构造语法支持，即包含某个参数时SQL语句包含某一段
     *
     * @param sql
     *            原SQL，其中用${macroName}形式表示宏参数
     * @param params
     *            查询参数，含宏参数。宏参数不能为NULL
     *
     * 如果SQL中含有宏标记，则将相应的参数作为宏进行替换。同时params中的宏参数将移除 如:
     * params={field1:"AAAA",field2:"BBBB"} sql="SELECT
     * A.${field1},A.${field1},COUNT(*) CNT FROM MY_TABLE GROUP BY
     * A.${field1},A.${field2} ORDER BY A.${field1},A.${field2} "; 执行结果为
     * sql="SELECT A.AAAA,A.BBBBCOUNT(*) CNT FROM MY_TABLE GROUP BY
     * A.AAAA,A.BBBB ORDER BY A.AAAA,A.BBBB ";
     * @author 李科飞
     */
    public void replaceMacro(StringBuffer sql, Map params) {
        int k = sql.indexOf(MACRO_BEGIN);
        if (k < 0) {
            return;
        }
        int j = 0;
        Set macroNames = new HashSet();
        while (k > 0) {
            j = sql.indexOf(MACRO_END, k + 2);
            int m = sql.indexOf(MACRO_BEGIN, k + 2);
            if (j < 0) {
                throw new IllegalArgumentException("SQL宏语法解析错误，缺少【}】，位置：" + k + "\n" + sql.substring(k));
            }
            if (m > 0 && m < j) {
                throw new IllegalArgumentException("SQL宏语法错误，【${】和【}】不匹配，位置：" + k + "\n" + sql.substring(k, j + 1));
            }
            String macroName = sql.substring(k + 2, j).trim();
            macroNames.add(macroName);
            // 判断条件中是否包含了参数
            if (params.containsKey(macroName)) {
                String pValue = params.get(macroName).toString();
                sql.replace(k, j + 1, pValue);
                k = k + pValue.length();
                j = k;
            } else {// 不包含,删除语法块.改为不删除，以避免在语句中存在的${}
                // sql.delete(k, j + 1);
                // j = k;
                k = j + 2;
            }
            k = sql.indexOf(MACRO_BEGIN, k);
        }
        Iterator it = macroNames.iterator();
        while (it.hasNext()) {
            params.remove(it.next());
        }
    }
    @Override
    public Iterator<ELEM> iterator(DbCondition<String> condition) {
        return null;
    }

    @Override
    public PageInfo<ELEM> queryForPage(DbCondition<String> condition, PageInfo<ELEM> pageInfo) {
        PageInfo page = pageInfo;
        if (page == null) {
            page = new PageInfo();
        }

        if(condition.getElemClass()!=null){
            return queryForPage(condition.getSql(), condition.getParams(), Long.valueOf(page.getTotalCount()).intValue(), page.getPageSize(), page.getPageIndex(), null, null);
        }
        return   sqlQueryForPage(condition.getSql(), condition.getParams(), Long.valueOf(page.getTotalCount()).intValue(),
                page.getPageSize(), page.getPageIndex(), null, null);


    }

    @Override
    public PageInfo<Map<String, Object>> queryForMapPage(DbCondition<String> condition, PageInfo<Map<String, Object>> pageInfo) {
        return null;
    }

    /**
     * 执行JDBC语句分页查询的方法，参数使用命名参数方式传递，放在一个MAP对象中
     *
     * @param sql
     *            查询sql
     * @param params
     *            查询参数
     * @param totalCount
     *            总记录数
     * @param pageSize
     *            分页记录数
     * @param pageIndex
     *            页码
     * @return 查询结果对象分页列表
     * @author likf
     */

    public PageInfo sqlQueryForPage(String sql, Map params, int totalCount, int pageSize, int pageIndex,
                                    String sortField, String sortDirect) {
        if (sortField != null && !sortField.equals("") && sql.contains("${sortField}")) {
            String sortExpr = sortField + " " + (sortDirect == null ? "" : sortDirect);
            //如果排序字段中含有$符号，会出错
            sortExpr=sortExpr.replace("$", "\\$");
            sql=sql.replaceAll("\\$\\{sortField\\}", sortExpr);
        }
        String sqlStr = sqlParse(new StringBuffer(sql), params);
        PageInfo pageInfo = new PageInfo(pageSize, pageIndex, totalCount);
      /*  pageInfo.setSortField(sortField);
        pageInfo.setSortDirect(sortDirect);*/

       // Session session = openSession();
        Session session = getSession();
        try {
            if (totalCount == 0) {
                String temp = sqlStr.replaceAll("ORDER BY.*", "");
                temp = temp.replaceAll("order by.*", "");
                String tSql = "select count(*) \"TOTALCOUNT\" from (" + temp + ") T";
                pageInfo.setTotalCount(Integer.parseInt(((Map) sqlQueryForMap1(session,tSql, params)).get("TOTALCOUNT")
                        .toString()));
            } else {
                pageInfo.setTotalCount(totalCount);
            }
            if (pageIndex == 0) {
                pageInfo.setPageIndex(1);
            } else {
                pageInfo.setPageIndex(pageIndex);
            }
            pageInfo.setPageSize(pageSize);
           // session = getSession();
            SQLQuery sqlQuery = this.getSqlQuery(session, sqlStr, params);
            if (pageInfo.getPageSize() != -1) {
                sqlQuery.setFirstResult((pageInfo.getPageIndex() - 1) * pageInfo.getPageSize());
                sqlQuery.setMaxResults(pageInfo.getPageSize());
            }
            sqlQuery.setResultTransformer(ALIAS_TO_ENTITY_MAP);
            pageInfo.setDataList(sqlQuery.list());
            if (pageInfo.getPageSize() == -1) {
                pageInfo.setPageCount(1);
                pageInfo.setPageIndex(1);
                pageInfo.setTotalCount(pageInfo.getDataList().size());
            }
        } catch (HibernateException e) {
            LOGGER.error(sql);
            e.printStackTrace();
            throw e;
        } finally {
           /* session.flush();
            session.close();*/
        }
        return pageInfo;
    }



    /**
     * 单记录查询,返回字段名为主键的MAP
     *
     * @param queryStr
     *            查询sql
     * @return Map
     */
    public Map sqlQueryForMap(String queryStr) {
        return sqlQueryForMap(queryStr, null);
    }
    /**
     * 单记录查询,返回字段名为主键的MAP
     *
     * @param queryStr
     *            查询sql
     * @param params
     *            查询参数
     * @return Map
     * @author likf
     */
    public Map sqlQueryForMap(String queryStr, Map params) {
        List list = sqlQueryForList(queryStr, params);
        if (list != null && list.size() > 0) {
            return (Map) list.get(0);
        } else {
            return new HashMap();
        }
    }

    public Map sqlQueryForMap1(Session session,String queryStr, Map params) {
        List list = sqlQueryForList1(session,queryStr, params);
        if (list != null && list.size() > 0) {
            return (Map) list.get(0);
        } else {
            return new HashMap();
        }
    }

    public List sqlQueryForList1(Session session,String queryStr, Map params) {

        try {
            String sql = sqlParse(new StringBuffer(queryStr), params);
            SQLQuery sqlQuery = this.getSqlQuery(session, sql, params);
            sqlQuery.setResultTransformer(ALIAS_TO_ENTITY_MAP);
            return sqlQuery.list();
        } catch (HibernateException e) {
            LOGGER.error(queryStr);
            e.printStackTrace();
            throw e;
        }
    }
    /**
     * 通过命名参数执行JDBC语句查询列表的方法
     *
     * @param queryStr
     *            查询SQL
     * @param params
     *            查询参数
     * @return List
     * @author likf
     */
    public List sqlQueryForList(String queryStr, Map params) {
       // Session session = openSession();
        Session session = getSession();
        try {
            String sql = sqlParse(new StringBuffer(queryStr), params);
            SQLQuery sqlQuery = this.getSqlQuery(session, sql, params);
            sqlQuery.setResultTransformer(ALIAS_TO_ENTITY_MAP);
            return sqlQuery.list();
        } catch (HibernateException e) {
            LOGGER.error(queryStr);
            e.printStackTrace();
            throw e;
        } finally {
          /*  session.flush();
            session.close();*/
        }
    }
    /**
     * hibernate 分页查询：
     *
     * 一般查询语句：如select a from tablea a where conditionExpr order by orderExpr
     * 生成的总数查询语句为 select count(*) from tablea a where conditionExpr
     * 分组汇总查询HQL语句没法生成总数查询语句(因为hql不支持在查询中用子查询)，
     * 只能查询全部记录得到记录数。性能很差应避免使用，此时应通过变通的方法先求出总数然后再进行分页查询。
     *
     * @param hql
     *            查询hql,如果包含排序字段则用${sortField}宏表示
     * @param params
     *            查询参数
     * @param totalCount
     *            总记录数，为0时自动查询总记录数，否则不再查询总记录数
     * @param pageSize
     *            分页记录数
     * @param pageIndex
     *            页码
     * @return 分页数据
     * @author  likf
     */
    public PageInfo queryForPage(String hql, Map params, int totalCount, int pageSize, int pageIndex,
                                 String sortField, String sortDirect) {
        String hql1 = hql;
        if (sortField != null && !sortField.equals("") && hql.contains("${sortField}")) {
            String sortExpr = sortField + " " + (sortDirect == null ? "" : sortDirect);
            //如果排序字段中含有$符号，会出错
            sortExpr=sortExpr.replace("$", "\\$");
            hql1=hql.replaceAll("\\$\\{sortField\\}", sortExpr);
        }
        hql1 = sqlParse(new StringBuffer(hql1), params);
        PageInfo pageInfo = new PageInfo();

        try {
            if (pageIndex == 0) {
                pageInfo.setPageIndex(1);
            } else {
                pageInfo.setPageIndex(pageIndex);
            }
            pageInfo.setPageSize(pageSize);

            if (totalCount == 0) {// hql 查询总记录数处理，简单sql(含group bya
                // 语句将会查询所有记录来求得总数)
                // hql = hql.replaceAll("FROM ", "from ");
                // hql = hql.replaceAll("FROM ", "from ");
                // hql = hql.replaceAll("GOUPR BY ", "group by ");
                // hql = hql.replaceAll("Group by ", "group by ");
                // hql = hql.replaceAll("Group By ", "group by ");
                Pattern p1 = Pattern.compile("from", Pattern.CASE_INSENSITIVE);
                hql1 = p1.matcher(hql1).replaceAll("from");

                Pattern p2 = Pattern.compile("[\\s]*group[\\s]*by *", Pattern.CASE_INSENSITIVE);
                hql1 = p2.matcher(hql1).replaceAll(" group by ");

                Pattern p4 = Pattern.compile("[\\s]* distinct[\\s] *", Pattern.CASE_INSENSITIVE);
                hql1 = p4.matcher(hql1).replaceAll(" distinct ");

                // Pattern p3 = Pattern.compile("[\\s]*order[\\s]*by[\\s\\S]*",
                // Pattern.CASE_INSENSITIVE);
                // hql = p3.matcher(hql).replaceAll("");

                String tSql = "select count(*) " + hql1.substring(hql1.indexOf("from"));
                if (hql1.indexOf("distinct ") > 0) {
                    tSql = hql1;
                }
                tSql = tSql.replaceAll("(?i)order by .*", "");
                if (hql1.lastIndexOf("group by ") > 0 || hql1.indexOf("distinct ") > 0) {
                    Query tQuery = this.getQuery(tSql, params);
                    pageInfo.setTotalCount(tQuery.list().size());
                } else {
                    Query tQuery = this.getQuery(tSql, params);
                    pageInfo.setTotalCount(Integer.valueOf(tQuery.list().get(0).toString()).intValue());
                }

            } else {
                pageInfo.setTotalCount(totalCount);
            }

            Query query = this.getQuery(hql1, params);
            if (pageInfo.getPageSize() != -1) {
                query.setFirstResult((pageInfo.getPageIndex() - 1) * pageInfo.getPageSize());
                query.setMaxResults(pageInfo.getPageSize());
            }
            pageInfo.setDataList(query.list());
            if (pageInfo.getPageSize() == -1) {// -1表示不分页
                pageInfo.setPageCount(1);
                pageInfo.setPageIndex(1);
                pageInfo.setTotalCount(pageInfo.getDataList().size());
            }
        } catch (HibernateException e) {
            LOGGER.error(hql1);
            e.printStackTrace();
            throw e;
        }

        return pageInfo;
    }


    @Override
    public Object rawExecute(DbCondition<String> condition) {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
