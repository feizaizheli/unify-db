package cn.com.nwdc.db.es.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


/**
 * @author: zhangyr
 * datetime: 2019-09-26 10:22
 * description: 自定义的一些配置属性
 */
@Data
@Configuration
public class EsConfig {

    //    Fi-es索引分片数
    @Value("${spring.es.fi.shardnum}")
    private String shardNum;
    //    Fi-es索引分
    @Value("${spring.es.fi.replicanum}")
    private String replicaNum;
    //    Fi-es类型
    @Value("${spring.es.type}")
    private String fiType;


    @Value("${spring.es.template.path:/noSql/}")
    private String templatePath;


}
