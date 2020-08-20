package cn.com.nwdc.db.index.common;


import com.alibaba.fastjson.JSONObject;

import freemarker.core.Environment;
import freemarker.template.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author coffee
 * @Classname ESFtlTemplate
 * @Description TODO
 * @Date 2020/7/17 10:56
 */

public class IndexDbTemplate implements Closeable {

    private static Logger LOGGER = LoggerFactory.getLogger(IndexDbTemplate.class);

    private final static  String TEMPLATE_FTL = "ftl";

    private final static Map<String, Template> templateMap = new HashMap<>(30);

    private static Configuration ftlConfig;



    /**
     * 初始化
     */
    public static void loadResources(String resourcePath) {
        LOGGER.info("Load es noSql resourcePath:{}",resourcePath);
        URL url = IndexDbTemplate.class.getResource(resourcePath);
        if(url == null){
            LOGGER.warn("Es noSql template ftl resourcePath is empty");
            return;
        }
        String path = url.getPath();
        LOGGER.info("Load es noSql template file, path ==>{}",path);
        ftlConfig = new Configuration(Configuration.VERSION_2_3_28);
        ftlConfig.setClassLoaderForTemplateLoading(IndexDbTemplate.class.getClassLoader(), resourcePath);
        ftlConfig.setDefaultEncoding("UTF-8");
        ftlConfig.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
        ftlConfig.setLogTemplateExceptions(false);

        List<String> tmpFileList = new ArrayList<>();
        try {
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            Resource[] metaInfResources = resourcePatternResolver.getResources("classpath*:"+resourcePath+"/*."+TEMPLATE_FTL);
            for(Resource r : metaInfResources){
                String fileName = r.getFilename();
                if (fileName.endsWith(IndexDbTemplate.TEMPLATE_FTL)){
                    tmpFileList.add(r.getFilename());
                }
                LOGGER.info("Es noSql template file list :" +fileName);
            }

        }catch (Exception e){
            e.printStackTrace();
            LOGGER.error("Load es noSql template error:"+e.getMessage());
        }
        if(tmpFileList.size() == 0){
            LOGGER.warn("Es noSql template ftl file is empty");
        }
        for (String templateFileName : tmpFileList) {
            Template template = null;
            try {
                template = ftlConfig.getTemplate(templateFileName);
                if(templateMap.containsKey(templateFileName)){
                    LOGGER.warn("[{}] already exsits!",templateFileName);
                    continue;
                }

                LOGGER.info("[TEMPLATE:{}] load suceess!",templateFileName);
                templateMap.put(templateFileName,template);
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.warn("[FTL:{}]load error!", templateFileName);
            }

        }

    }



    public static Template findTemplateByName(String templateName){
        Template template = templateMap.get(templateName);
        if(template==null){
            try {
                template = ftlConfig.getTemplate(templateName);
                templateMap.put(templateName,template);
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.warn("TemplateDir not find template[{}]  !",templateName);
                return null;
            }
        }
        return templateMap.get(templateName);

    }
    /**
     * 根据模板文件输出内容到控制台
     * @param templateName       模板文件的名称
     * @param methodName 方法名称
     * @param rootMap    模板的数据模型
     * @return
     */
    public static String SQL(String templateName, String methodName, Map<String,Object> rootMap){
        try {
            if(rootMap==null){
                rootMap = new HashMap<>(10);
            }
            if(!templateName.endsWith(TEMPLATE_FTL)){
                templateName = templateName +"."+TEMPLATE_FTL;
            }
            rootMap.put("select",new SelectDirective(methodName));
            StringWriter writer = new StringWriter();
            Template template = findTemplateByName(templateName);
            template.process(rootMap, writer);
            String jsonStr = writer.toString().replaceAll("[\u0000]", "");
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            String esSql = jsonObject.toJSONString();
            LOGGER.info("Read template[{}][{}] esNoSql:[{}]",templateName,methodName,esSql);
            return esSql;
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.error("Read template[{}][{}] error!",templateName,methodName);
            throw new RuntimeException("Read template["+templateName+"]["+methodName+"] error!");
        }
    }

    @Override
    public void close() throws IOException {

    }

    /**
    * @author linwh
    *
    * date 2020/7/1 9:52
    *
    * description
    */

    public static class SelectDirective implements TemplateDirectiveModel {

        private String methodName;

        private boolean isNew;

        private boolean finish;

        public SelectDirective() {
        }

        public SelectDirective(String methodName) {
            this.methodName = methodName;
        }

        private static Logger logger = LoggerFactory.getLogger(SelectDirective.class);
        @Override
        public void execute(Environment environment, Map params, TemplateModel[] templateModels, TemplateDirectiveBody templateDirectiveBody) throws TemplateException, IOException {
            isNew = false;
            if(!finish){
                if(templateDirectiveBody==null){
                    throw new RuntimeException("method body cannot empty!");
                }
                templateDirectiveBody.render(new SelectWriter(environment.getOut()));
            }
        }

        private class SelectWriter extends Writer {

            private final Writer out;

            SelectWriter(Writer out){
                this.out = out;
            }

            @Override
            public void write(char[] childrenBuffer, int off, int len) throws IOException {
                String value = String.valueOf(childrenBuffer,off,len);
                if(!StringUtils.isEmpty(value)){
                    if(value.contains(methodName)||isNew){
                        value = value.replaceAll(methodName+":","");
                        isNew = true;
                        out.write(value);
                        finish = true;
                    }
                }
            }
            @Override
            public void flush() throws IOException {

            }

            @Override
            public void close() throws IOException {

            }
        }
    }
}
