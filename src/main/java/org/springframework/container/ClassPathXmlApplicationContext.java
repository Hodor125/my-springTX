package org.springframework.container;

import org.springframework.annotation.*;
import org.springframework.proxy.JdkProxy;
import org.springframework.xml.SpringConfigPaser;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author ：hodor007
 * @date ：Created in 2021/2/7
 * @description ：
 * @version: 1.0
 */
public class ClassPathXmlApplicationContext {
    //传入配置文件名
    private String springConfig;

    //存放所有的全类名
    private List<String> classPathes = new ArrayList<>();

    //存放对象名和对应的实例，就是springIOC容器，根据名字查找
    private Map<String, Object> iocNameContainer = new ConcurrentHashMap<>();

    //ioc容器，以class文件作为key
    private Map<Class<?>, Object> iocContainer = new ConcurrentHashMap<>();

    //springIoc容器，对象实现的接口作为key，接口的是实现类作为value
    private Map<Class<?>, List<Object>> iocInterface = new ConcurrentHashMap<>();

    //存放AOP切面类的集合，线程安全
    private Set<Class<?>> aopClassSet = new CopyOnWriteArraySet<>();


    private Set<Class<?>> proxyClassSet = new CopyOnWriteArraySet<>();

    public ClassPathXmlApplicationContext(String springConfig) {
        this.springConfig = springConfig;
        init();
    }

    /**
     * 1. 解析xml文件获取需要扫描的包
     */
    private void init() {
        String basePackage = SpringConfigPaser.getBasePackage(springConfig);
        System.out.println("component-scan<=======>" + basePackage);
        loadClasses(basePackage);
    }

    /**
     * 2. 获取需要扫描的包的文件路径
     * 3. 裁取类的全路径如 com.hodor.controller.OrderController
     * 4. 反射创建类
     * 5. 实现对象的依赖注入
     * @param basePackage 传入值为com.hodor
     */
    private void loadClasses(String basePackage) {
        //2. 获取需要扫描的包的文件路径 E:\code\git\my-springIOC\target\classes\com\hodor
        //扫描con.hodor下的包
        //将.替换为\
        basePackage = basePackage.replace(".", File.separator);
        System.out.println("replace<========>" + basePackage);
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        System.out.println("url<=======>" + url);
        String replace_url = url.toString().replace("file:/", "");
//        System.out.println("replace url<=======>" + replace_url);
        if(replace_url.contains("test-")) {
            replace_url = replace_url.replace("test-", "");
        }
        File file = new File(replace_url, basePackage);
        System.out.println("file<=========>" + file);

        //3. 裁取类的全路径如 com.hodor.controller.OrderController
        findAllClasses(file);

        //4. 反射创建类
        doInitInstance();

        //AOP切入，注入容器的实例化对象是代理增强过的对象
        doAOP();

        //5. 实现对象的依赖注入
        doDi();
        System.out.println("iocNameContainer<=====>" + iocNameContainer);
        System.out.println("iocContainer<=====>" + iocContainer);
        System.out.println("iocInterface<=====>" + iocInterface);
    }

    /**
     * 3. 裁取类的全名称如 com.hodor.controller.OrderController
     * 扫描文件路径下所有的class文件，输出文件的全路径，需要进行替换才能用于反射创建对象
     * 将类的全路径存入集合中
     */
    private void findAllClasses(File file) {
        File[] files = file.listFiles();
        for (File f : files) {
            if(f.isDirectory()) {
                findAllClasses(f);
            } else {
                String path = f.getPath();
                if(path.endsWith(".class")) {
//                    System.out.println("original classpath<======>" + path);
                    int index1 = path.lastIndexOf("\\classes");
                    int index2 = path.lastIndexOf(".class");
                    path = path.substring(index1 + 9, index2).replace("\\", ".");
                    classPathes.add(path);
                    //得到类的路径，用于反射创建对象
//                    System.out.println("classpath<======>" + path);
                }
            }
        }
        //拿到文件的路径    E:\code\git\my-springIOC\target\classes\com\hodor\service\impl\OrderServiceImpl.class
        //通过反射创建类    只需要com后面的路径，类似于com.hodor.service.impl.OrderServiceImpl
    }

    /**
     * 4. 使用类的全名称反射创建类，有注解的类进行实例化
     * 判断有注解的才需要实例化
     * 需要考虑注解中有value的情况，就不能使用默认的类名作为对象名
     */
    private void doInitInstance() {
        try {
            for (String classPath : classPathes) {
                Class<?> c = Class.forName(classPath);

                //定位到类的Aspect注解
                if(c.isAnnotationPresent(Aspect.class)) {
                    //存储切面类
                    aopClassSet.add(c);
                    continue;
                }

                //判断实例化的这个类是否带了注解
                if(c.isAnnotationPresent(Controller.class) || c.isAnnotationPresent(Service.class) || c.isAnnotationPresent(Repository.class)) {
                    //实例化
                    Object instance = c.newInstance();
                    Controller controllerAnno = c.getAnnotation(Controller.class);
                    if(controllerAnno != null) {
                        String value = controllerAnno.value();
                        String simpleName = "";
                        //如果注解有值就使用指定的值作为名字创建对象
                        if(value == null || "".equals(value)) {
                            simpleName = c.getSimpleName();
                            simpleName = simpleName.toLowerCase().charAt(0) + simpleName.substring(1);
                        } else {
                            simpleName = value;
                        }
//                        System.out.println("simpleName<=======>" + simpleName);
                        //扩展可以使用三种不同的方式获取对象
                        //通过对象名字获取对象，重复的对象名就报错
                        if(iocNameContainer.containsKey(simpleName)) {
                            throw new Exception("The bean name had already existed in the container");
                        }
                        iocNameContainer.put(simpleName, instance);
                        //通过class文件过去对象
                        iocContainer.put(c, instance);

                        //通过接口获取对象
                        Class<?>[] interfaces = c.getInterfaces();
                        for (Class<?> anInterface : interfaces) {
                            List<Object> byInterface = iocInterface.get(anInterface);
                            if(byInterface != null) {
                                byInterface.add(instance);
                                iocInterface.put(anInterface, byInterface);
                            } else {
                                List<Object> byInstance = new ArrayList<>();
                                byInstance.add(instance);
                                iocInterface.put(anInterface, byInstance);
                            }
                        }
                    }

                    Service serviceAnnotation = c.getAnnotation(Service.class);
                    if(serviceAnnotation != null) {
                        String simpleName = "";
                        String value = serviceAnnotation.value();
                        if(value == null || "".equals(value)) {
                            simpleName = c.getSimpleName();
                            simpleName = simpleName.toLowerCase().charAt(0) + simpleName.substring(1);
                        } else {
                            simpleName = value;
                        }
//                        System.out.println("simpleName<=======>" + simpleName);
                        if(iocNameContainer.containsKey(simpleName)) {
                            throw new Exception("The bean name had already existed in the container");
                        }
                        iocNameContainer.put(simpleName, instance);
                        iocContainer.put(c, instance);

                        Class<?>[] interfaces = c.getInterfaces();
                        for (Class<?> anInterface : interfaces) {
                            List<Object> byInterface = iocInterface.get(anInterface);
                            if(byInterface != null) {
                                byInterface.add(instance);
                                iocInterface.put(anInterface, byInterface);
                            } else {
                                List<Object> byInstance = new ArrayList<>();
                                byInstance.add(instance);
                                iocInterface.put(anInterface, byInstance);
                            }
                        }
                    }

                    Repository repositoryAnno = c.getAnnotation(Repository.class);
                    if(repositoryAnno != null) {
                        String simpleName = "";
                        String value = repositoryAnno.value();
                        if(value == null || "".equals(value)) {
                            simpleName = c.getSimpleName();
                            simpleName = simpleName.toLowerCase().charAt(0) + simpleName.substring(1);
                        } else {
                            simpleName = value;
                        }
                        if(iocNameContainer.containsKey(simpleName)) {
                            throw new Exception("The bean name had already existed in the container");
                        }
                        iocNameContainer.put(simpleName, instance);
                        iocContainer.put(c, instance);

                        Class<?>[] interfaces = c.getInterfaces();
                        for (Class<?> anInterface : interfaces) {
                            List<Object> byInterface = iocInterface.get(anInterface);
                            if(byInterface != null) {
                                byInterface.add(instance);
                                iocInterface.put(anInterface, byInterface);
                            } else {
                                List<Object> byInstance = new ArrayList<>();
                                byInstance.add(instance);
                                iocInterface.put(anInterface, byInstance);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过对象名从容器中获取对象
     * @param name
     * @return
     */
    public Object getBean(String name) {
        return iocNameContainer.get(name);
    }

    /**
     * 根据类类获取对象
     * @param aClass
     * @return
     */
    public Object getBean(Class<?> aClass) {
        return iocContainer.get(aClass);
    }

    public Object getBeanByInterface(Class<?> aClass) {
        List<Object> objects = iocInterface.get(aClass);
        if(objects == null) {
            return null;
        }

        //如果这个接口有多个是实现类就报错，因为无法确定是哪一个类
        if(objects.size() > 1) {
            throw new RuntimeException("The bean named '" + aClass + "available: expected single matching bean but not found");
        }
        return objects.get(0);
    }

    /**
     * 时机：在讲类的实例对象放入容器后，进行@autowired依赖注入之前
     * 执行AOP操作，将在ioc容器中切面表达式execution对应的类对应的方法进行动态代理增强，然后将增强后的对象放入容器中
     * 在4中定位Aspect注解，存储了AOP类
     */
    private void doAOP() {
        //取出execution的值，遍历切面类集合
        if(aopClassSet.size() > 0) {
            try {
                //切面类class
                for (Class<?> aClass : aopClassSet) {
                    //获取切面类中的方法
                    Method[] declaredMethods = aClass.getDeclaredMethods();
                    if(declaredMethods != null && declaredMethods.length > 0) {
                        int flag = 0;
                        for (Method method : declaredMethods) {
                            if(method.isAnnotationPresent(Around.class)) {
                                Around around = method.getAnnotation(Around.class);
                                //得到切入点表达式
                                String execution = around.execution();
                                int index = execution.lastIndexOf(".");
                                //要代理的目标类
                                String fullClass = execution.substring(0, index);
                                //要代理的方法名称
                                String methodName = execution.substring(index + 1);
                                //要代理的目标class
                                Class<?> targetClass = Class.forName(fullClass);
                                //要被代理的类的对象
                                /**
                                 * 提前进行依赖注入
                                 */
                                proxyClassSet.add(targetClass);

                                if(flag == 0) {
                                    doDiByClass(targetClass);
                                    flag++;
                                }
                                Object targetObject = iocContainer.get(targetClass);
                                //把targetObject的目标方法进行代理（包裹上增强的代码）
                                JdkProxy<Object> jdkProxy = new JdkProxy<Object>(targetClass, aClass, targetObject, methodName, method);
                                //代理后的对象，和OrderServiceImpl是兄弟关系
                                Object proxyInstance = jdkProxy.getProxy();
                                //proxyName<=======>$Proxy9
//                                System.out.println("proxyName<=======>" + proxyInstance.getClass().getSimpleName());
                                //把三种原始容器中的对象替换为被代理的对象\
                                iocContainer.put(targetClass, proxyInstance);
                                String simpleName = targetClass.getSimpleName();
                                String className = simpleName.toLowerCase().charAt(0) + simpleName.substring(1);
                                Service service = targetClass.getAnnotation(Service.class);
                                Controller controller = targetClass.getAnnotation(Controller.class);
                                Repository repository = targetClass.getAnnotation(Repository.class);
                                if(service != null) {
                                    String value = service.value();
                                    if(!"".equals(value)) {
                                        className = value;
                                    }
                                }
                                if(controller != null) {
                                    String value = controller.value();
                                    if(!"".equals(value)) {
                                        className = value;
                                    }
                                }
                                if(repository != null) {
                                    String value = repository.value();
                                    if(!"".equals(value)) {
                                        className = value;
                                    }
                                }
                                iocNameContainer.put(className, proxyInstance);
                                Class<?>[] interfaces = targetClass.getInterfaces();
                                if(interfaces != null) {
                                    for (Class<?> anInterface : interfaces) {
                                        List<Object> objects = iocInterface.get(anInterface);
                                        if(objects != null) {
                                            for (int i = 0; i < objects.size(); i++) {
                                                if(objects.get(i).getClass() == targetClass) {
                                                    objects.set(i, proxyInstance);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 5. 实现依赖注入
     */
    private void doDi() {
        Set<Class<?>> classes = iocContainer.keySet();
        if(classes != null) {
            //通过class遍历所有的对象
            for (Class<?> aClass : classes) {
                if(!proxyClassSet.contains(aClass)) {
                    doDiByClass(aClass);
                }
            }
        }
    }

    /**
     * 根据class进行依赖注入
     * @param aClass
     */
    private void doDiByClass(Class<?> aClass) {
        //获取声明的属性的集合
        Field[] declaredFields = aClass.getDeclaredFields();
        if(declaredFields != null) {
            for (Field declaredField : declaredFields) {
                if(declaredField.isAnnotationPresent(Autowired.class)) {
                    //类中的属性需要依赖注入，给属性赋值，三种情况都要考虑（根据名字、class、接口从容器中找对象）
                    Autowired autowired = declaredField.getAnnotation(Autowired.class);
                    String value = autowired.value();
                    Object bean = null;
                    //如果@Autowired的属性不为空
                    if(!"".equals(value)) {
                        bean = getBean(value);
                        if(bean == null) {
                            throw new RuntimeException("No bean of this type name '" + value + "' available;expected at least 1 bean in the container");
                        }
                    } else {
                        //获取字段的类型
                        Class<?> keyClass = declaredField.getType();
                        //根据class文件获取，比如直接声明属性是接口实现类的情况，或者直接声明属性不带接口（如本例的controller）
                        bean = getBean(keyClass);
                        if(bean == null) {
                            //如果根据class找不到，就根据接口类匹
                            bean = getBeanByInterface(keyClass);
                            if(bean == null) {
                                throw new RuntimeException("No qualifying bean of type '" + aClass + "' available");
                            }
                        }
                    }

                    try {
                        //注入的属性在容器中匹配到了，通过反射注入属性，设置权限为可以设置
                        declaredField.setAccessible(true);
                        //通过class方式获取到对象比较合适，三种方式获取到的对象是相同的（如果能获取到）
                        declaredField.set(iocContainer.get(aClass), bean);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
