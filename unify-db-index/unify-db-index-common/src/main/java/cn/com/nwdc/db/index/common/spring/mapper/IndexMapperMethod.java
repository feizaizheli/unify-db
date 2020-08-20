package cn.com.nwdc.db.index.common.spring.mapper;

import cn.com.nwdc.db.anno.RootObject;
import cn.com.nwdc.db.anno.cache.TableInfoHelper;
import cn.com.nwdc.db.anno.cache.info.TableInfo;
import cn.com.nwdc.db.index.common.anno.ParamTableName;
import cn.com.nwdc.db.index.common.anno.ParamTableNameStrategyBean;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Map;

public interface IndexMapperMethod {

    public static final String MAPPER = "Mapper";


    public Object execute();





    public static class MethodSignature{


        private Object proxy;
        private Method method;
        private Class<?> returnType;
        private Type genericReturnType;

        private Object[] argVals;
        private String[] argKeys;

        private String name;

        private String tableName;

        private Map<String,Object> params;
        private RootObject rootObject;


        public MethodSignature(Object proxy,Method method, Object[] argVals) {
            this.proxy = proxy;
            this.method = method;
            this.returnType = method.getReturnType();
            this.argVals = argVals;
            this.name = method.getName();
            this.genericReturnType = method.getGenericReturnType();
            if(method.isAnnotationPresent(RootObject.class)){
                rootObject = method.getAnnotation(RootObject.class);
            }
            initParams();
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public Class<?> getReturnType() {
            return returnType;
        }

        public void setReturnType(Class<?> returnType) {
            this.returnType = returnType;
        }


        public Object getProxy() {
            return proxy;
        }

        public void setProxy(Object proxy) {
            this.proxy = proxy;
        }

        public Map<String,Object> getParams(){
            return this.params;
        }


        public RootObject getRootObject() {
            return rootObject;
        }

        public void initParams(){
           /* ParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
            String[] parameterNames=discoverer.getParameterNames(method);*/
            Parameter[] parameters = method.getParameters();
            if(parameters == null || parameters.length<1){
                return;
            }
            if(params == null){
                params = Maps.newHashMap();
            }


            String[] argKeys = new String[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                argKeys[i] = parameters[i].getName();
                if(StringUtils.isBlank(tableName)){
                    ParamTableName paramTableName = parameter.getAnnotation(ParamTableName.class);
                    if(paramTableName!=null ){
                        this.tableName = String.valueOf(argVals[i]);
                    }else{
                        ParamTableNameStrategyBean paramTableNameStrategyBean
                                = parameter.getAnnotation(ParamTableNameStrategyBean.class);
                        if(paramTableNameStrategyBean!=null){
                            TableInfo tableInfo = TableInfoHelper.getTableInfo(argVals[i].getClass());
                            if(tableInfo.getTableNameStrategy()==null){
                                this.tableName = tableInfo.getTableName();
                            }else{
                                this.tableName = tableInfo.getTableNameStrategy().getTableName(tableInfo.getTableName(),argVals[0]);
                            }

                        }
                    }
                }


            }

            for (int i = 0; i < argKeys.length; i++) {
                params.put(argKeys[i],argVals[i]);
            }

        }

      /*  public List<String> groupKeys(){
            Parameter parameter = null;
            List<String> groups = null;
            for (int i = 0; i < method.getParameters().length; i++) {
                parameter = method.getParameters()[i];
                ParamGroupKey paramGroupKey = parameter.getAnnotation(ParamGroupKey.class);
                if(paramGroupKey!=null){
                    if(groups == null){
                        groups = new LinkedList<>();
                    }
                    groups.add(parameter.getName());
                }
            }
            return groups;
        }*/

        public String getTableName(){

            return this.tableName;
        }

        public Object[] getArgVals() {
            return argVals;
        }

        public void setArgVals(Object[] argVals) {
            this.argVals = argVals;
        }

        public Type getGenericReturnType() {
            return genericReturnType;
        }

        public void setGenericReturnType(Type genericReturnType) {
            this.genericReturnType = genericReturnType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
