package cn.com.nwdc.unify.db;

import cn.com.nwdc.unify.db.index.common.anno.IndexMapperScan;
import cn.com.nwdc.unify.db.index.common.spring.IndexSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@IndexMapperScan("cn.com.nwdc.unify.db.mapper")
public class UnifyDbTestApplication {

    @Autowired
    private IndexSessionFactoryBean indexSessionFactoryBean;

    public static void main(String[] args) {
        SpringApplication.run(UnifyDbTestApplication.class, args);

    }



}
