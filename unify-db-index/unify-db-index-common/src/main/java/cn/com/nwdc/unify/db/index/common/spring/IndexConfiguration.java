package cn.com.nwdc.unify.db.index.common.spring;

import cn.com.nwdc.unify.db.index.common.spring.proxy.IndexMapperRegistry;
import com.baomidou.mybatisplus.MybatisConfiguration;
import com.baomidou.mybatisplus.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.entity.GlobalConfiguration;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

/**
 * @author coffee
 * @Classname IndexConfiguration
 * @Description TODO
 * @Date 2020/8/25 14:45
 */
public class IndexConfiguration  extends Configuration {

    private static final Log logger = LogFactory.getLog(MybatisConfiguration.class);


    /*
     * Mapper 注册
     */
    public final IndexMapperRegistry indexMapperRegistry = new IndexMapperRegistry(this);

    /**
     * 初始化调用
     */
    public IndexConfiguration() {
        System.err.println("index init success.");
    }

    /**
     * <p>
     * MybatisPlus 加载 SQL 顺序：
     * </p>
     * 1、加载XML中的SQL<br>
     * 2、加载sqlProvider中的SQL<br>
     * 3、xmlSql 与 sqlProvider不能包含相同的SQL<br>
     * <br>
     * 调整后的SQL优先级：xmlSql > sqlProvider > curdSql <br>
     */
    @Override
    public void addMappedStatement(MappedStatement ms) {
        logger.debug("addMappedStatement: " + ms.getId());
        if (GlobalConfiguration.getGlobalConfig(ms.getConfiguration()).isRefresh()) {
            /*
             * 支持是否自动刷新 XML 变更内容，开发环境使用【 注：生产环境勿用！】
             */
            this.mappedStatements.remove(ms.getId());
        } else {
            if (this.mappedStatements.containsKey(ms.getId())) {
                /*
                 * 说明已加载了xml中的节点； 忽略mapper中的SqlProvider数据
                 */
                logger.error("mapper[" + ms.getId() + "] is ignored, because it's exists, maybe from xml file");
                return;
            }
        }
        super.addMappedStatement(ms);
    }

    @Override
    public void setDefaultScriptingLanguage(Class<?> driver) {
        if (driver == null) {
            /* 设置自定义 driver */
            driver = MybatisXMLLanguageDriver.class;
        }
        super.setDefaultScriptingLanguage(driver);
    }


    public IndexMapperRegistry getIndexMapperRegistry() {

        return indexMapperRegistry;
    }

    @Override
    public <T> void addMapper(Class<T> type) {
        indexMapperRegistry.addMapper(type);
    }

    @Override
    public void addMappers(String packageName, Class<?> superType) {
        indexMapperRegistry.addMappers(packageName, superType);
    }

    @Override
    public void addMappers(String packageName) {
        indexMapperRegistry.addMappers(packageName);
    }

    @Override
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return indexMapperRegistry.getMapper(type, sqlSession);
    }

    @Override
    public boolean hasMapper(Class<?> type) {
        return indexMapperRegistry.hasMapper(type);
    }
}
