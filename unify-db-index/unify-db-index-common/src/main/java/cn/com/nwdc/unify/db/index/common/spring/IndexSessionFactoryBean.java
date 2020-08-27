package cn.com.nwdc.unify.db.index.common.spring;

import com.baomidou.mybatisplus.MybatisXMLMapperBuilder;
import lombok.Data;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import static org.springframework.util.ObjectUtils.isEmpty;

/**
 * @author coffee
 * @Classname IndexSessionFactoryBean
 * @Description TODO
 * @Date 2020/8/26 9:54
 */
@Data
@Service
public class IndexSessionFactoryBean<T>  implements FactoryBean<T>, InitializingBean{

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexSessionFactoryBean.class);

    private Resource[] mapperLocations;

    private Configuration configuration;

    @Override
    public T getObject() throws Exception {
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public void afterPropertiesSet(){

        try {
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            mapperLocations = resourcePatternResolver.getResources("classpath*:mapper/*.xml");

        }catch (Exception e){
            e.printStackTrace();
            LOGGER.error("Load es noSql template error:"+e.getMessage());
        }
        buildIndexSession();
    }


    public void buildIndexSession() {

        if(configuration == null){
            configuration = new IndexConfiguration();
        }

        if (!isEmpty(this.mapperLocations)) {
            for (Resource mapperLocation : this.mapperLocations) {
                if (mapperLocation == null) {
                    continue;
                }
                try {
                    MybatisXMLMapperBuilder xmlMapperBuilder = new MybatisXMLMapperBuilder(
                            mapperLocation.getInputStream(),
                            configuration,
                            mapperLocation.toString(),
                            configuration.getSqlFragments());
                    xmlMapperBuilder.parse();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to parse mapping resource: '" + mapperLocation + "'", e);
                } finally {
                    ErrorContext.instance().reset();
                }

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Parsed mapper file: '" + mapperLocation + "'");
                }
            }
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Property 'mapperLocations' was not specified or no matching resources found");
            }
        }
    }
}
