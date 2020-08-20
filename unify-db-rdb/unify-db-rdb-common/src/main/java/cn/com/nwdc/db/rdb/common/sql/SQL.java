package cn.com.nwdc.db.rdb.common.sql;

import cn.com.nwdc.db.anno.cache.info.TableFieldInfo;
import cn.com.nwdc.db.anno.cache.info.TableInfo;
import com.google.common.collect.Maps;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author coffee
 * @Classname SQL
 * @Description TODO
 * @Date 2019/11/5 11:30
 */
public class SQL {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQL.class);

    public static String TABLE_NAME = "$TableName";
    public static String KEY_SQL = "$KeySQL";
    public static String PARAM_SQL = "$ParamSQL";
    public static String PRIMARY_KEY = "$PrimaryKey";
    public static String SQL_PARAMS="#{params.";


    public static String SQL_PAMAMS_END = "}";

    public static final String SQL_VALUES = "values(";

    public static interface Builder{

    }

    public static enum Type {
        INSERT("insert into $TableName($KeySQL) values($ParamSQL)"),
        UPDATE("update $TableName set $ParamSQL where $PrimaryKey=?"),
        DELETE("delete from $TableName where $PrimaryKey=?"),
        COPY_MANAGER("Copy $TableName($KeySQL) from STDIN DELIMITER ',' ENCODING 'utf-8'"),
       // QUERY("select * from  $TableName where 1=1");
       QUERY("select * from  $TableName where $PrimaryKey");
        private String sql;

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }

        Type(String sql) {
            this.sql = sql;
        }
    }


    public static class SQLFactory {

        private static Map<Type, Class<? extends ISQLBuilder>> sqlBuilderMap = Maps.newHashMap();

        static {

            sqlBuilderMap.put(Type.INSERT, INSERT.class);
            sqlBuilderMap.put(Type.DELETE, DELETE.class);
            sqlBuilderMap.put(Type.UPDATE, UPDATE.class);

        }


        public static <ELEM> ISQLBuilder build(Type type, ELEM elem) {

            if (!sqlBuilderMap.containsKey(type)) {
                throw new IllegalArgumentException("无此类型[" + type.name() + "]");

            }
            try {
                Class<? extends ISQLBuilder> sqlBuilderClass = sqlBuilderMap.get(type);
                ISQLBuilder sqlBuilder = sqlBuilderClass.newInstance();
                sqlBuilder.setElem(elem);
                return sqlBuilder;

            } catch (Throwable throwable) {
                throwable.printStackTrace();
                LOGGER.error("构建ISQLBuilder[{}] ERROR",type.name());
            }

            return null;
        }
    }

    @Data
    public static class FieldEntry {
        private Object pkValue;
        private List<Object> fieldValues = new ArrayList<>();
    }

    public static class UPDATE<ELEM> extends AbstractSQLBuilder<ELEM> {


        public UPDATE(ELEM elem) {
            super(elem);
        }

        public static <ELEM> UPDATE build(ELEM elem) {
            return new UPDATE(elem);
        }
        @Override
        public String doKeySQL(String sql, TableInfo tableInfo) {

            StringBuffer paramSql = new StringBuffer();
            for (TableFieldInfo tableFieldInfo : tableInfo.getFieldList()) {
                paramSql.append(tableFieldInfo.getColumn()).append(" =?,");
            }
            paramSql.deleteCharAt(paramSql.length() - 1);
            sql = sql.replace(PARAM_SQL, paramSql).replace(PRIMARY_KEY, tableInfo.getKeyColumn());
            return sql;
        }


        @Override
        public FieldEntry valSQL(ELEM elem) {
            return buildValSql(elem, new FieldEntry());
        }

        @Override
        public Type getType() {
            return Type.UPDATE;
        }

        @Override
        public String keyFieldSQL() {
            return null;
        }

        @Override
        public String valFieldSQL(String valPrefix, String valEnd) {

            StringBuffer valSql = new StringBuffer();
            for(TableFieldInfo tableFieldInfo:tableInfo.getFieldList()){
                valSql.append(tableFieldInfo.getColumn()).append("= #{params.").append(tableFieldInfo.getProperty()).append("}").append(",");
            }
            valSql.deleteCharAt(valSql.length()-1);
            valSql.append("where ").append(tableInfo.getKeyColumn()).append("= ").append("'").append(valEnd).append("'");
            return valSql.toString();
        }
    }

    public static class INSERT<ELEM> extends AbstractSQLBuilder<ELEM> {


        public INSERT(ELEM elem) {
            super(elem);
        }

        public INSERT() {

        }



        public static <ELEM> INSERT build(ELEM elem) {
            return new INSERT(elem);
        }

        @Override
        public String doKeySQL(String sql, TableInfo tableInfo) {
            StringBuffer paramSql = new StringBuffer("?,").append(getParamSQL(tableInfo));
            sql = sql.replace(PARAM_SQL, paramSql).replace(KEY_SQL, getKeySQL(tableInfo));
            return sql;
        }

        @Override
        public FieldEntry valSQL(ELEM elem) {
            return buildValSql(elem, new FieldEntry());
        }

        @Override
        public Type getType() {
            return Type.INSERT;
        }

        @Override
        public String keyFieldSQL( ) {
            StringBuffer keySql = new StringBuffer();
            keySql.append(tableInfo.getKeyColumn()).append(",");
            for(TableFieldInfo tableFieldInfo:tableInfo.getFieldList()){
                if(tableFieldInfo.getColumn()!=null){
                    keySql.append(tableFieldInfo.getColumn()).append(",");
                }

            }
            keySql.deleteCharAt(keySql.length()-1);
            return keySql.toString();
        }

        @Override
        public String valFieldSQL(String valPrefix,String valEnd) {

            StringBuffer valSql = new StringBuffer();
            valSql.append(valPrefix).append(tableInfo.getKeyProperty()).append(valEnd).append(",");
            for(TableFieldInfo tableFieldInfo:tableInfo.getFieldList()){
                if(tableFieldInfo.getColumn()!=null){
                    valSql.append(valPrefix).append(tableFieldInfo.getProperty()).append("}").append(",");
                }

            }
            valSql.deleteCharAt(valSql.length()-1);
            return valSql.toString();
        }


    }


    public static class DELETE<ELEM> extends AbstractSQLBuilder<ELEM> {

        public DELETE(ELEM elem) {
            super(elem);
        }


        public static <ELEM> DELETE build(ELEM elem) {
            return new DELETE(elem);
        }

        @Override
        public String doKeySQL(String sql, TableInfo tableInfo) {
            return sql.replace(TABLE_NAME, tableInfo.getTableName()).replace(PRIMARY_KEY, tableInfo.getKeyColumn());
        }

        @Override
        public FieldEntry valSQL(ELEM elem) {
            return null;
        }

        @Override
        public Type getType() {
            return Type.DELETE;
        }

        @Override
        public String keyFieldSQL() {
            StringBuffer valSql = new StringBuffer();
            valSql.append(tableInfo.getKeyColumn());
            return valSql.toString();
        }

        @Override
        public String valFieldSQL(String valPrefix, String valEnd) {
           return null;
        }
    }


    public static class CM<ELEM> extends AbstractSQLBuilder<ELEM> {


        public static CM build() {
            return new CM();
        }

        @Override
        public String doKeySQL(String sql, TableInfo tableInfo) {

            return sql.replace(KEY_SQL, getKeySQL(tableInfo));
        }



         @Override
         public FieldEntry valSQL(ELEM elem) {
            return buildValSql(elem, new FieldEntry());
        }

        @Override
        public Type getType() {
            return Type.COPY_MANAGER;
        }

        @Override
        public String keyFieldSQL() {
            return null;
        }

        @Override
        public String valFieldSQL(String valPrefix, String valEnd) {
            return null;
        }
    }

    public static class QUERY<ELEM> extends AbstractSQLBuilder<ELEM> {

        public QUERY(ELEM elem) {
            super(elem);
        }

        public static <ELEM> QUERY build(ELEM elem) {
            return new QUERY(elem);
        }

        @Override
        public String doKeySQL(String sql, TableInfo tableInfo) {
            StringBuffer paramSql = new StringBuffer();
            paramSql.append(" and ").append(tableInfo.getKeyColumn()).append(" =?,");
            for (TableFieldInfo tableFieldInfo : tableInfo.getFieldList()) {
                paramSql.append(" and ").append(tableFieldInfo.getColumn()).append(" =?,");
            }
            paramSql.deleteCharAt(paramSql.length()-1);
            sql = sql.replace(PARAM_SQL, paramSql).replace(TABLE_NAME, tableInfo.getTableName());
            return sql;
        }

        @Override
        public FieldEntry valSQL(ELEM elem) {
            return buildValSql(elem, new FieldEntry());
        }

        @Override
        public Type getType() {
            return Type.QUERY;
        }

        @Override
        public String keyFieldSQL() {
            StringBuffer keySql = new StringBuffer();
            keySql.append(tableInfo.getKeyColumn());
            return keySql.toString();
        }

        @Override
        public String valFieldSQL(String valPrefix, String valEnd) {
            StringBuffer valSql = new StringBuffer();
            valSql.append(valPrefix).append(tableInfo.getKeyProperty()).append(valEnd);
            return valSql.toString();
        }
    }


}


