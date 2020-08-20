package cn.com.nwdc.utils;



import org.apache.ibatis.javassist.ClassPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import sun.reflect.generics.reflectiveObjects.WildcardTypeImpl;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author coffee
 * @Classname DbElement
 * @Description TODO
 * @Date 2019/9/7 17:01
 */

public class ClassUtil {

    public static final Logger LOGGER = LoggerFactory.getLogger(ClassUtil.class);

    private static ClassPool pool = ClassPool.getDefault();

    static {

    }

    public static <T extends Annotation> T getAntation(Class<?> clazz, Class<? extends Annotation> annotationClass){

        if (clazz.isAnnotationPresent(annotationClass)) {
            return (T)clazz.getAnnotation(annotationClass);
        }
        return null;
    }

    /**
     * 从包package中获取所有的Class
     *
     * @param pack
     * @return
     */
    public static Set<Class<?>> getClasses(String pack) {

        // 第一个class类的集合
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        // 是否循环迭代
        boolean recursive = true;
        // 获取包的名字 并进行替换
        String packageName = pack;
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
                // 得到协议的名称
                String protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    // System.err.println("file类型的扫描");
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    // 如果是jar包文件
                    // 定义一个JarFile
                    // System.err.println("jar类型的扫描");
                    JarFile jar;
                    try {
                        // 获取jar
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        // 从此jar包 得到一个枚举类
                        Enumeration<JarEntry> entries = jar.entries();
                        // 同样的进行循环迭代
                        while (entries.hasMoreElements()) {
                            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            // 如果是以/开头的
                            if (name.charAt(0) == '/') {
                                // 获取后面的字符串
                                name = name.substring(1);
                            }
                            // 如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                // 如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    // 获取包名 把"/"替换成"."
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                // 如果可以迭代下去 并且是一个包
                                if ((idx != -1) || recursive) {
                                    // 如果是一个.class文件 而且不是目录
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        // 去掉后面的".class" 获取真正的类名
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            // 添加到classes
                                            classes.add(Class.forName(packageName + '.' + className));
                                        } catch (ClassNotFoundException e) {
                                            // log
                                            // .error("添加用户自定义视图类错误
                                            // 找不到此类的.class文件");
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        // log.error("在扫描用户定义视图时从jar包获取文件出错");
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive,
                                                        Set<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            // log.warn("用户定义包名 " + packageName + " 下没有任何文件");
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            @Override
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        // 循环所有文件
        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive,
                        classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    // 添加到集合中去
                    // classes.add(Class.forName(packageName + '.' +
                    // className));
                    // 经过回复同学的提醒，这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
                    classes.add(
                            Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    // log.error("添加用户自定义视图类错误 找不到此类的.class文件");
                    e.printStackTrace();
                }
            }
        }
    }



    /**
     * 根据字段名获取字段值
     *
     * @param fieldName
     * @param object
     * @return
     */
    public static Object getFieldValue(String fieldName, Object object) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException("[Field:"+fieldName+"]不存在");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    /**
     * 根据字段名获取字段值
     *
     * @param fieldName
     * @param object
     * @return
     */
    public static Field getField(String fieldName, Object object) {
        try {
            return object.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }


    }

    public static Annotation[] getAnnotation(Class type, Class annotation) {

        List<Annotation> list = new ArrayList<Annotation>();
        Class<?> clazz = type;
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                if (clazz.getAnnotation(annotation) != null) {
                    list.add(clazz.getAnnotation(annotation));
                }
            } catch (Exception e) {
                // 这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                // 如果这里的异常打印或者往外抛，则就不会执行clazz =
                // clazz.getSuperclass(),最后就不会进入到父类中了

            }
        }
        return list.toArray(new Annotation[]{});
    }

    /**
     * 通过反射,获得定义Class时声明的父类的范型参数的类型.
     * 如public BookManager extends GenricManager<Book>
     *
     * @param clazz clazz The class to introspect
     * @param index the Index of the generic ddeclaration,start from 0.
     */
    @SuppressWarnings("rawtypes")
    public static Class getSuperClassGenericity(Class clazz, int index) throws IndexOutOfBoundsException {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class) params[index];
    }

    public static String getDefBeanName(Class<?> classType) {
        String name = classType.getSimpleName();
        String firstChat = name.substring(0, 1).toLowerCase();
        return firstChat + name.subSequence(1, name.length());
    }

    public static<T> T instanceByConstructor(Class<T> tClass,Object... constructorParams) throws Throwable{
        Class[] constructorTypes = new Class[constructorParams.length];
        for (int i = 0; i < constructorParams.length; i++) {
            constructorTypes[i] = constructorParams[i].getClass();
        }
        Constructor cla = tClass.getDeclaredConstructor(constructorTypes);
        return (T) cla.newInstance(constructorParams);
    }

    public static String classPackageAsResourcePath(Class<?> clazz) {
        if (clazz == null) {
            return "";
        }
        String className = clazz.getName();
        int packageEndIndex = className.lastIndexOf('.');
        if (packageEndIndex == -1) {
            return "";
        }
        String packageName = className.substring(0, packageEndIndex);
        return packageName.replace('.', '/');
    }


    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back to system class loader...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = ClassUtil.class.getClassLoader();
        }
        return cl;
    }



    /**
     * 获取类型属性的泛型类型
     *
     * @return
     */
    public static Class<?>[] getTypeGenericity(Type fc) {
        if (fc == null) {
            return new Class<?>[]{};
        }
        // 【3】如果是泛型参数的类型
        if (fc instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) fc;
            Type[] ts = pt.getActualTypeArguments();
            List<Class<?>> t = new ArrayList<Class<?>>();
            for (int i = 0; i < ts.length; i++) {
                if (ts[i] instanceof WildcardTypeImpl) {
                    //泛型可能是 <?>
                } else {
                    t.add((Class<?>) ts[i]);
                }

            }
            return t.toArray(new Class<?>[]{});
        } else {
            return new Class<?>[]{};
        }
    }


    public static boolean isListType(Field field) {
        return isListType(field.getType());
    }

    public static boolean isListType(Class<?> objType) {
        Class<?>[] interfaces = objType.getInterfaces();
        for (Class<?> i : interfaces) {
            if (i.equals(List.class)) {
                return true;
            }
        }
        return objType.isAssignableFrom(List.class);
    }

    public static boolean isMapType(Field field) {
        Class<?> fieldClazz = field.getType();
        return isMapType(fieldClazz);
    }

    public static boolean isMapType(Class<?> objType) {
        return isType(objType, Map.class);
    }

    public static boolean isSetType(Field field) {
        Class<?> fieldClazz = field.getType();
        return isSetType(fieldClazz);
    }

    public static boolean isSetType(Class<?> objType) {
        return isType(objType, Set.class);
    }

    /**
     * @return
     */
    public static boolean isType(Class<?> parentType, Class<?> child) {
        return parentType.isAssignableFrom(child);
    }

    /**
     * 获取List类型属性的泛型类型
     *
     * @param field
     * @return
     */
    public static Type getSetFieldGenericity(Field field) {
        Class<?> fieldClazz = field.getType();
        Assert.isTrue(isSetType(fieldClazz), "属性类型必须为Set类型");
        Type fc = field.getGenericType(); // 关键的地方，如果是List类型，得到其Generic的类型
        if (fc == null) {
            return null;
        }
        // 【3】如果是泛型参数的类型
        if (fc instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) fc;
            return pt.getActualTypeArguments()[0]; // 【4】
        } else {
            return null;
        }
    }


    /**
     * 获取List类型属性的泛型类型
     *
     * @param field
     * @return
     */
    public static Type[] getMapFieldGenericity(Field field) {
        Class<?> fieldClazz = field.getType();
        Assert.isTrue(isMapType(fieldClazz), "属性类型必须为Map类型");
        Type fc = field.getGenericType(); // 关键的地方，如果是List类型，得到其Generic的类型
        if (fc == null) {
            return null;
        }
        // 【3】如果是泛型参数的类型
        if (fc instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) fc;
            return pt.getActualTypeArguments(); // 【4】
        } else {
            return null;
        }
    }

    /**
     * 获取List类型属性的泛型类型
     *
     * @param field
     * @return
     */
    public static Class<?> getListGenericity(Field field) {
        Class<?> fieldClazz = field.getType();
        Assert.isTrue(isListType(fieldClazz), "属性类型必须为List类型");
        Type fc = field.getGenericType(); // 关键的地方，如果是List类型，得到其Generic的类型
        if (fc == null) {
            return null;
        }
        // 【3】如果是泛型参数的类型
        if (fc instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) fc;
            return (Class<?>) pt.getActualTypeArguments()[0]; // 【4】
        } else {
            return null;
        }
    }

    /**
     * 获取List类型属性的泛型类型
     *
     * @param field
     * @return
     */
    public static Class<?> getListFieldGenericity(Field field) {
        Class<?> fieldClazz = field.getType();
        Assert.isTrue(fieldClazz.isAssignableFrom(List.class), "属性类型必须为List类型");
        Type fc = field.getGenericType(); // 关键的地方，如果是List类型，得到其Generic的类型
        if (fc == null) {
            return null;
        }
        // 【3】如果是泛型参数的类型
        if (fc instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) fc;
            return (Class<?>) pt.getActualTypeArguments()[0]; // 【4】
        } else {
            return null;
        }
    }

    /**
     * 判断属性名是否存在避免调用	getField(String name) 抛异常
     *
     * @param type
     * @return
     */
    public static boolean existField(Class<?> type, Class<?> fieldType) {
        if (fieldType == null) {
            return false;
        }
        Field[] fs = getDeclaredFields(type);
        for (Field field : fs) {
            if (field.getType().equals(fieldType)) {
                return true;
            }
        }
        return false;
    }


    public static Method getDeclaredMethod(Object object, String methodName, Class<?>... parameterTypes) {
        Method method = null;
        for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                clazz.getMethods();
                method = clazz.getMethod(methodName, parameterTypes);
                return method;
            } catch (Exception e) {
                //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了

            }
        }
        return null;
    }

    public static Method[] getDeclaredMethods(Object object) {
        List<Method> list = new ArrayList<Method>();
        for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                for (Method method : clazz.getDeclaredMethods()) {
                    if (!Modifier.isStatic(method.getModifiers())) {
                        list.add(method);
                    }
                }
            } catch (Exception e) {
                //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了

            }
        }
        return list.toArray(new Method[]{});
    }

    /**
     * 递归获取所有的接口
     *
     * @param clazz
     * @return
     */
    public static Class<?>[] getInterfaces(Class<?> clazz) {
        List<Class<?>> list = new LinkedList<Class<?>>();
        Class<?>[] interfaces = clazz.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            Class ifs = interfaces[i];
            list.add(ifs);
            list.addAll(Arrays.asList(getInterfaces(ifs)));
        }
        return list.toArray(new Class[list.size()]);
    }


    /**
     * 直接调用对象方法, 而忽略修饰符(private, protected, default)
     *
     * @param object         : 子类对象
     * @param methodName     : 父类中的方法名
     * @param parameterTypes : 父类中的方法参数类型
     * @param parameters     : 父类中的方法参数
     * @return 父类中方法的执行结果
     */

    public static Object invokeMethod(Object object, String methodName, Class<?>[] parameterTypes,
                                      Object[] parameters) {
        //根据 对象、方法名和对应的方法参数 通过反射 调用上面的方法获取 Method 对象
        Method method = getDeclaredMethod(object, methodName, parameterTypes);
        Assert.notNull(method, "方法[" + methodName + "]不允许为空");
        //抑制Java对方法进行检查,主要是针对私有方法而言
        method.setAccessible(true);
        try {
            if (null != method) {
                // 调用object 的 method 所代表的方法，其方法的参数是 parameters
                return method.invoke(object, parameters);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("方法执行异常，异常消息[" + e.getMessage() + "]", e);
        }
        return null;
    }


    /**
     * 包括父类的属性
     *
     * @param type
     * @return
     */
    public static Field[] getDeclaredFields(Class<?> type) {
        List<Field> list = new ArrayList<Field>();
        Class<?> clazz = type;
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                for (Field field : clazz.getDeclaredFields()) {
                    if (!Modifier.isStatic(field.getModifiers())) {
                        list.add(field);
                    }
                }
            } catch (Exception e) {
                // 这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                // 如果这里的异常打印或者往外抛，则就不会执行clazz =
                // clazz.getSuperclass(),最后就不会进入到父类中了

            }
        }
        return list.toArray(new Field[]{});
    }


    public static Field[] getDeclaredFields(Class<?> type, Class a) {
        List<Field> fs = new ArrayList<>();
        Field[] fields = getDeclaredFields(type);
        for (Field field : fields) {
            if (field.getType().getAnnotation(a) != null) {
                fs.add(field);
            }
        }
        return fs.toArray(new Field[]{});
    }

    /**
     * 包括父类的属性
     *
     * @param type
     * @param fieldName
     * @return
     */
    public static Field getDeclaredFields(Class<?> type, String fieldName) {
        Field field = null;
        Class<?> clazz = type;
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                return field;
            } catch (Exception e) {
                // 这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                // 如果这里的异常打印或者往外抛，则就不会执行clazz =
                // clazz.getSuperclass(),最后就不会进入到父类中了

            }
        }
        return null;
    }

    /**
     * 判断属性名是否存在避免调用	getField(String name) 抛异常
     *
     * @param type
     * @param fieldName
     * @return
     */
    public static boolean existField(Class<?> type, String fieldName) {
        if (fieldName == null) {
            return false;
        }
        return getDeclaredFields(type, fieldName) != null;
    }

    /**
     * 递归寻找属性的类型
     *
     * @param beanType 根类型
     * @param fieldStr 如bean.field.field
     * @return
     */
    public static final Class<?>[] getTypes(Class<?> beanType, String fieldStr) {
        Assert.notNull(beanType, "根类型不允许为空");
        Assert.hasText(fieldStr, "属性串不允许为空");
        String[] fieldNames = fieldStr.split("\\.");
        List<Class<?>> returnTypes = new ArrayList<Class<?>>();

        Class<?> returnType = beanType;
        for (String fn : fieldNames) {
            if (ClassUtil.existField(returnType, fn)) {
                Field f;
                f = getDeclaredFields(returnType, fn);
                returnTypes.add(f.getType());
                returnType = f.getType();
            } else {
                return returnTypes.toArray(new Class<?>[0]);
            }
        }
        return returnTypes.toArray(new Class<?>[0]);
    }


    public static final boolean isBaseType(Field field) {
        Assert.notNull(field, "属性不允许为空！");
        return isBaseType(field.getType());
    }





    public static final boolean isBaseType(Class<?> beanType) {
        Assert.notNull(beanType, "类型不允许为空！");
        return beanType.isPrimitive()
                || String.class.equals(beanType)
                || Long.class.equals(beanType)
                || Short.class.equals(beanType)
                || Character.class.equals(beanType)
                || Integer.class.equals(beanType)
                || Double.class.equals(beanType)
                || Boolean.class.equals(beanType)
                || Float.class.equals(beanType);
    }



    /**
     * 递归寻找属性的类型
     *
     * @param beanType 根类型
     * @param fieldStr 如bean.field.field
     * @return
     */
    public static final Class<?> getType(Class<?> beanType, String fieldStr) {
        Assert.notNull(beanType, "根类型不允许为空");
        Assert.hasText(fieldStr, "属性串不允许为空");
        String[] fieldNames = fieldStr.split("\\.");
        Class<?> returnType = beanType;
        for (String fn : fieldNames) {
            if (ClassUtil.existField(returnType, fn)) {
                Field f;
                try {
                    f = getDeclaredFields(returnType, fn);
                    returnType = f.getType();
                } catch (NullPointerException e) {
                    //找不到字段返回NULL
                    e.printStackTrace();
                    return null;
                }
            } else {
                return null;
            }
        }
        return returnType;
    }

    /**
     * 获取某个包下面的class
     *
     * @param pack
     * @return
     */
    public static List<String> getClasssFromPackage(String pack) {
        List<String> classNames = new ArrayList<String>();
        // 是否循环搜索子包
        boolean recursive = true;
        // 包名字
        String packageName = pack;
        // 包名对应的路径名称
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    System.out.println("file类型的扫描");
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findClassInPackageByFile(packageName, filePath, recursive, classNames);
                } else if ("jar".equals(protocol)) {
                    System.out.println("jar类型的扫描");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classNames;
    }

    private static void findClassInPackageByFile(String packageName, String filePath, final boolean recursive, List<String> clazzNames) {
        File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 在给定的目录下找到所有的文件，并且进行条件过滤
        File[] dirFiles = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                boolean acceptDir = recursive && file.isDirectory();// 接受dir目录
                boolean acceptClass = file.getName().endsWith("class");// 接受class文件
                return acceptDir || acceptClass;
            }
        });

        for (File file : dirFiles) {
            if (file.isDirectory()) {
                findClassInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, clazzNames);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    clazzNames.add(packageName + "." + className);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 从jar文件中读取指定目录下面的所有的class文件
     *
     * @param jarPath  jar文件存放的位置
     * @param jarPath 指定的文件目录
     * @return 所有的的class的对象
     */
    public static List<String> getClasssFromJarFile(String jarPath) {
        List<String> classNameList = new ArrayList<>();
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(jarPath);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        List<JarEntry> jarEntryList = new ArrayList<>();
        Enumeration<JarEntry> ee = jarFile.entries();
        while (ee.hasMoreElements()) {
            JarEntry entry = ee.nextElement();
//                entry.getName().startsWith(filePath) &&
            if (entry.getName().endsWith(".class")) {
                jarEntryList.add(entry);
            }
        }
        for (JarEntry entry : jarEntryList) {
            String className = entry.getName().replace('/', '.');
            className = className.substring(0, className.length() - 6);
            classNameList.add(className);
        }
        return classNameList;
    }

    public static Class[] getMethodParamTypes(Object classInstance, String methodName)  {
        Class[] paramTypes = null;
        try {
            Method[] methods = classInstance.getClass().getMethods();//全部方法
            for (int i = 0; i < methods.length; i++) {
                if (methodName.equals(methods[i].getName())) {//和传入方法名匹配
                    Class[] params = methods[i].getParameterTypes();
                    paramTypes = new Class[params.length];
                    for (int j = 0; j < params.length; j++) {
                        paramTypes[j] = Class.forName(params[j].getName());
                    }
                    break;
                }
            }
        }catch (Throwable e) {
            throw new IllegalStateException("获取方法参数类型失败", e);
        }
        return paramTypes;
    }

    public static Method findDeclaredMethod(Object object, String methodName) {
        Method execMethod = null;
        for (Method method : object.getClass().getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                execMethod = method;
                break;
            }
        }
        return execMethod;
    }



}
