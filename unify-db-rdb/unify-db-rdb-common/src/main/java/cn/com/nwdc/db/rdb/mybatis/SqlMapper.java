package cn.com.nwdc.db.rdb.mybatis;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.cursor.Cursor;

import java.util.List;
import java.util.Map;

/**
 * @author coffee
 * @Classname SqlMapper
 * @Description TODO
 * @Date 2020/3/13 21:13
 */

public interface SqlMapper<ELEM> extends BaseMapper {


  /*  @Insert("${sql}")
    Integer batchInsert(@Param("sql") String sql);
*/
    @Insert("insert into ${tableName}(${tableField} ) values(${valSql})")
    Integer insert(@Param("tableName") String tableName, String tableField, Object valSql, Object params);


    @Delete("delete from ${tableName} where ${primaryKey} in (${primaryKeyVal})")
    Integer delete(@Param("tableName") String tableName, String primaryKey, Object primaryKeyVal);


    @Delete("delete from ${tableName}")
    Integer deleteTable(@Param("tableName") String tableName);

    @Update("update ${tableName} set ${valSql}")
    Integer update(@Param("tableName") String tableName, Object valSql, @Param("params") Object params);


    @Select("${sql}")
    List<Map<String,Object>> selectList(@Param("sql") String sql);
    @Select("${sql}")
    Map<String,Object> selectElem(@Param("sql") String sql);

    @Select("select * from ${tableName} where ${primaryKey} = ${primaryKeyVal}")
    List<Map<String,Object>> selectListByElem(@Param("tableName") String tableName, String primaryKey, Object primaryKeyVal, Object params);

    @Select("select * from ${tableName} where ${primaryKey} = ${primaryKeyVal}")
    Map<String,Object> selectElemByCondition(@Param("tableName") String tableName, String primaryKey, Object primaryKeyVal, Object params);

    @Select("${sql}")
    Cursor<ELEM> selectCursor(@Param("sql") String sql);

}
