package cn.com.unify.db.rdb.oracle;


import cn.com.nwdc.db.rdb.mybatis.MybatisRdbOperator;
import org.springframework.stereotype.Component;

/**
 * @author heffb
 * @Classname RdbOracleQueryOperator
 * @Description TODO
 * @Date 2019/10/24 20:07
 * @group smart video north
 */
@Component
public class RdbOracleOperator<ELEM> extends MybatisRdbOperator<ELEM> {
 /*   @Override
    public PageInfo<ELEM> queryForPage(DbCondition condition, PageInfo<ELEM> pageInfo) {
        try {

            Connection connection = dataSource.getConnection();
            long totalCount = 0;
            //组装oracle的分页查询语句
            String[] pageSql=condition.getSql().toString().split("from");
            String sql="select * from ("+pageSql[0]+","+"rownum from"+pageSql[1]+")"+"where rownum >"+(pageInfo.getPageIndex()-1)*pageInfo.getPageSize()+
                    " and rownum <= "+pageInfo.getPageIndex()*pageInfo.getPageSize();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            Iterator<ELEM> iterator = new RdbDbIterator(rs, condition.getElemClass());
            ps = connection.prepareStatement("select count(1) from (" + condition.getSql() + ")");
            rs = ps.executeQuery();
            while (rs.next()) {
                totalCount = Long.parseLong(rs.getString(1));
            }
            pageInfo.setDataList(Lists.newArrayList(iterator));
            pageInfo.setTotalCount(totalCount);
            return pageInfo;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }*/
}
