package cn.com.nwdc.unify.db.es.config;

import lombok.Data;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;


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

    @Value("${spring.es.nodes}")
    private String nodes;
    @Value("${spring.es.schema}")
    private String schema;
    @Value("${spring.es.maxretrytimeoutmillis}")
    private int maxRetryTimeoutMillis;
    @Value("${spring.es.connecttimeout}")
    private int connectTimeout;
    @Value("${spring.es.sockettimeout}")
    private int socketTimeout;

    private List<HttpHost> httpHosts = new ArrayList<>();

    @PostConstruct
    public void init(){
        String[] esNodes = getNodes().split(",");
        for (String node : esNodes) {
            try {
                String[] parts = StringUtils.split(node, ":");
                Assert.notNull(parts, "Must defined");
                Assert.state(parts.length == 2, "Must be defined as 'host:port'");
                httpHosts.add(new HttpHost(parts[0], Integer.parseInt(parts[1]), schema));
            } catch (RuntimeException ex) {
                throw new IllegalStateException(
                        "Invalid ES nodes " + "property '" + node + "'", ex);
            }
        }
    }
    @Bean
    public RestClient getRestClient() {
        RestClient restClient = null;
        HttpHost[] httpHostArr = httpHosts.toArray(new HttpHost[0]);
        RestClientBuilder builder = RestClient.builder(httpHostArr);
        Header[] defaultHeaders = new Header[]{new BasicHeader("Accept", "application/json"),
                new BasicHeader("Content-type", "application/json")};
        builder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(connectTimeout);
            requestConfigBuilder.setSocketTimeout(socketTimeout);
            requestConfigBuilder.setConnectionRequestTimeout(maxRetryTimeoutMillis);
            return requestConfigBuilder;
        });
        builder.setDefaultHeaders(defaultHeaders);
        restClient = builder.build();
        return restClient;
    }

}
