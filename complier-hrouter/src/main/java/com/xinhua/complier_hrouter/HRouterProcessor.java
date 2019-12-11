package com.xinhua.complier_hrouter;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import com.xinhua.annotation_hrouter.HRouter;
import com.xinhua.annotation_hrouter.model.RouterBean;
import com.xinhua.complier_hrouter.utils.Constants;
import com.xinhua.complier_hrouter.utils.EmptyUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Created by 49944
 * Time: 2019/12/8 11:48
 * Des:
 * JavaPoet字符串格式化规则：
 * $L 字面量，如："int value = $L", 10
 * $S 字符串，如：$S, "Hello"
 * $T 类、接口， 如：$T, MainActivity
 * $N 变量， 如：user.$N, name
 * JavaPoet 8个常用类
 * 类对象         说明
 * MethodSpec     代表一个构造函数或方法声明
 * TypeSpec       代表一个类、接口，或者枚举声明
 * FieldSpe       代表一个成员变量，一个字段声明
 * JavaFile       包含一个顶级类的Java文件
 * ParameterSpec  用来创建参数
 * AnnotationSpec 用来创建注解
 * ClassName      用来包装一个类
 * TypeName       类型，如在添加返回值类型是使用TypeName.VOID
 *
 * @AutoService(Processor.class) 这个一定要有，不然不会执行自动生成代码的方法
 */
@AutoService(Processor.class)
@SupportedOptions({Constants.MODULE_NAME, Constants.APT_PACKAGE}) //接收的参数
//@SupportedSourceVersion(SourceVersion.RELEASE_7)
//@SupportedAnnotationTypes({Constants.HROUTER_ANNOTATION_TYPES})
public class HRouterProcessor extends AbstractProcessor {
    //操作Element工具类
    private Elements mElementUtils;
    //type(类信息) 工具类
    private Types mTypeUtils;
    //用来输出警告、错误等日志
    private Messager mMessager;
    //文件生成器
    private Filer mFiler;

    //子模块名 如：app/order/personal  需要拼接类名时用到（必填） HRouter$$Group$$order
    private String mModuleName;
    //包名： 用于存放apt生成的类文件
    private String mPackageNameForAPT;

    //临时map存储，用来存储路由组group对应的详细path类对象，生成路由路径类文件是遍历
    //key: 组名"app"  value: "app" 组的路由路径"HRouter$$Path$$app.class"
    private Map<String, List<RouterBean>> mTempPathMap = new HashMap<>();
    //临时map存储，用来存放路由Group信息，生成器由组类文件时遍历
    //key: 组名"app"  value:类名 "HRouter$$Path$$app.class"
    private Map<String, String> mTempGroupMap = new HashMap<>();


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mElementUtils = processingEnvironment.getElementUtils();
        mTypeUtils = processingEnvironment.getTypeUtils();
        mMessager = processingEnvironment.getMessager();
        mFiler = processingEnvironment.getFiler();

        Map<String, String> options = processingEnvironment.getOptions();
        if (!EmptyUtils.isEmpty(options)) {
            mModuleName = options.get(Constants.MODULE_NAME);
            mPackageNameForAPT = options.get(Constants.APT_PACKAGE);
            mMessager.printMessage(Diagnostic.Kind.NOTE, "moduleName:" + mModuleName + " packageName:" + mPackageNameForAPT);
        }
        if (EmptyUtils.isEmpty(mModuleName) || EmptyUtils.isEmpty(mPackageNameForAPT)) {
            throw new RuntimeException("注解处理器需要的参数moduleName或packageName为空, 请在build.gradle中配置参数");
        }
    }

    /**
     * 获取支持的注解类型（即需要去处理的注解类型）
     * 也可以在类上面用注解形式  如： @SupportedAnnotationTypes("com.xinhua.annotation_hrouter.HRouter")
     *
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        //需要处理的注解都可以在这里加
        annotations.add(HRouter.class);
        for (Class<? extends Annotation> annotation : annotations) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    /**
     * 用什么jdk的版本进行编译
     * 同样可以用注解的方式  @SupportedSourceVersion(SourceVersion.RELEASE_7)
     *
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * 相当于main函数，开始处理注解
     * 注解处理器的核心方法处理具体的注解，生成Java文件
     *
     * @param set              使用了支持处理注解的节点集合（类 上面写了注解）
     * @param roundEnvironment 当前或是之前的运行环境，可以通过该对象查找找到的注解
     * @return true 表示后续处理器不会再处理（已经处理完成）
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, "----> 进来了");
        if (set.isEmpty()) {
            return false;
        }
        //获取该项目中所有使用HRouter注解的节点
        Set<? extends Element> hrouterElements = roundEnvironment.getElementsAnnotatedWith(HRouter.class);
        //遍历所有的类节点
        if (!EmptyUtils.isEmpty(hrouterElements)) {
            try {
                parseElements(hrouterElements);
            } catch (IOException e) {
                e.printStackTrace();
            }

           /* for (Element element : hrouterElements) {
                //包名
                String packageName = mElementUtils.getPackageOf(element).getQualifiedName().toString();
                //类名
                String className = element.getSimpleName().toString();
                mMessager.printMessage(Diagnostic.Kind.NOTE, "被注解的类有个 -》包名：" + packageName + " 类名：" + className);
                //最终我们要生成的类文件， 如：MainActivity$$HRouter
                String finalClassName = className + "$$HRouter";

                HRouter hRouter = element.getAnnotation(HRouter.class);

                // 方法体 public static Class<?> findTargetClass(String path)
                MethodSpec methodSpec = MethodSpec.methodBuilder("findTargetClass")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(Class.class)
                        .addParameter(String.class, "path")
                        // return path.equalsIgnoreCase("/app/MainActivity") ? MainActivity.class : null;
                        .addStatement("return path.equalsIgnoreCase($S) ? $T.class : null",
                                hRouter.path(),
                                ClassName.get((TypeElement) element))
                        .build();
                // 构建类
                TypeSpec typeSpec = TypeSpec.classBuilder(finalClassName)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addMethod(methodSpec)
                        .build();
                JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                        .build();
                try {
                    javaFile.writeTo(mFiler);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }*/

        }
        return true;
    }

    private void parseElements(Set<? extends Element> elements) throws IOException {
        //获取Activity的类型
        TypeElement activityType = mElementUtils.getTypeElement(Constants.ACTIVITY);
        TypeMirror activityMirror = activityType.asType();

        for (Element element : elements) {
            //获取每个元素的类信息
            TypeMirror elementMirror = element.asType();
            mMessager.printMessage(Diagnostic.Kind.NOTE, "遍历的元素信息为：" + elementMirror.toString());
            //获取每个类上被@HRouter注解，对应的path值
            HRouter hRouter = element.getAnnotation(HRouter.class);
            //路由详细信息，封装成实体类
            RouterBean routerBean = new RouterBean.Builder()
                    .setGroup(hRouter.group())
                    .setPath(hRouter.path())
                    .setElement(element)
                    .build();

            //高级判断 被@HRouter注解仅仅只能用于类之上，并且必须是Activity的子类
            if (mTypeUtils.isSubtype(elementMirror, activityMirror)) {
                routerBean.setType(RouterBean.Type.ACTIVITY);
            } else {
                throw new RuntimeException("@HRouter目前仅限用于Activity之上");
            }
            valueOfPathMap(routerBean);
        }
        //HRouterLoadGroup和HRouterLoadPath 类型，用来生成类文件时实现接口
        TypeElement groupLoadType = mElementUtils.getTypeElement(Constants.HROUTER_GROUP);
        TypeElement pathLoadType = mElementUtils.getTypeElement(Constants.HROUTER_PATH);
        //1. 生成路由的详细path类文件， 如：HRouter$$Path$$app
        createPathFile(pathLoadType);
        //2. 生成路由组group类文件（没有path类文件，取不到）
        createGroupFile(groupLoadType, pathLoadType);
    }

    /**
     * 生成路由组Group对应的详细Path， 如：HRouter$$Path$$app
     *
     * @param pathLoadType
     */
    private void createPathFile(TypeElement pathLoadType) throws IOException {
        //判断临时map是否有需要生成的文件
        if (EmptyUtils.isEmpty(mTempPathMap)) {
            return;
        }
        //方法的返回值 Map<String, RouterBean>
        TypeName methodReturns = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouterBean.class)
        );
        //遍历分组，每一个分组创建一个路径类文件，如：HRoute$$Path$$app
        for (Map.Entry<String, List<RouterBean>> entry : mTempPathMap.entrySet()) {
            //方法体构造 public Map<String, RouterBean> loadPath(){}
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constants.PATH_METHOD_NAME)// 方法名
                    .addAnnotation(Override.class) //方法上的注解
                    .addModifiers(Modifier.PUBLIC) //public 修饰符
                    .returns(methodReturns);//返回值
            //不循环的部分 Map<String, RouterBean> pathMap = new HashMap<>();
            methodBuilder.addStatement("$T<$T, $T> $N = new $T<>()",
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(RouterBean.class),
                    Constants.PATH_PARAMETER_NAME,
                    ClassName.get(HashMap.class));

            List<RouterBean> pathList = entry.getValue();
            for (RouterBean bean : pathList) {
                //方法内容的循环部分
                /**
                 *  pathMap.put("order/Order_MainActivity",
                 *                 RouterBean.create(RouterBean.Type.ACTIVITY,
                 *                         Order_MainActivity.class,
                 *                         "order/OrderMainActivity",
                 *                         "order"));
                 */
                methodBuilder.addStatement("$N.put($S, $T.create($T.$L, $T.class, $S, $S))",
                        Constants.PATH_PARAMETER_NAME,  //pathMap.put
                        bean.getPath(), // "order/Order_MainActivity"
                        ClassName.get(RouterBean.class),
                        ClassName.get(RouterBean.Type.class),
                        bean.getType(), //枚举ACTIVITY
                        ClassName.get((TypeElement) bean.getElement()), //Order_MainActivity.class
                        bean.getPath(), // "order/OrderMainActivity"
                        bean.getGroup()); //"order"
            }
            //最后return pathMap;
            methodBuilder.addStatement("return $N", Constants.PATH_PARAMETER_NAME);

            //生成类文件，如：HRouter$$Path$$app
            String finalClassName = Constants.PATH_FILE_NAME + entry.getKey();
            mMessager.printMessage(Diagnostic.Kind.NOTE, "APT生成路由Path类文件为：" +
                    mPackageNameForAPT + "." + finalClassName);
            JavaFile javaFile = JavaFile.builder(mPackageNameForAPT,//包路径
                    TypeSpec.classBuilder(finalClassName) //类名
                            .addSuperinterface(ClassName.get(pathLoadType)) //实现接口
                            .addModifiers(Modifier.PUBLIC)
                            .addMethod(methodBuilder.build()) //方法体
                            .build())
                    .build();
            javaFile.writeTo(mFiler);
            //加入组
            mTempGroupMap.put(entry.getKey(), finalClassName);
        }
    }

    /**
     * 生成路由组Group文件，如：HRouter$$Group$$app
     *
     * @param groupLoadType HRouterLoadGroup接口信息
     * @param pathLoadType  HRouterLoadPath接口信息
     */
    private void createGroupFile(TypeElement groupLoadType, TypeElement pathLoadType) throws IOException {
        //判断是否需要生成的类文件
        if (EmptyUtils.isEmpty(mTempGroupMap) || EmptyUtils.isEmpty(mTempPathMap)) return;

        //返回值类型   Map<String, Class<? extends HRouterLoadPath>>
        TypeName methodReturns = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(pathLoadType))));

        //方法体配置： public Map<String, Class<? extends HRouterLoadPath>> loadGroup() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constants.GROUP_METHOD_NAME) //方法名
                .addAnnotation(Override.class) //添加重新的注解
                .addModifiers(Modifier.PUBLIC)
                .returns(methodReturns);

        //不需要重复的内容
        //Map<String, Class<? extends HRouterLoadPath>> groupMap = new HashMap<>();
        methodBuilder.addStatement("$T<$T, $T> $N = new $T<>()",
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(pathLoadType))),
                Constants.GROUP_PARAMETER_NAME,
                HashMap.class);
        //方法内容配置 需要循环添加的部分
        for (Map.Entry<String, String> entry : mTempGroupMap.entrySet()) {
            // groupMap.put("order", HRouter$$Path$$Order.class);
            methodBuilder.addStatement("$N.put($S, $T.class)",
                    Constants.GROUP_PARAMETER_NAME, // groupMap.put
                    entry.getKey(),
                    //类文件在指定包名下
                    ClassName.get(mPackageNameForAPT, entry.getValue()));
        }
        //遍历之后，添加返回值  return groupMap;
        methodBuilder.addStatement("return $N", Constants.GROUP_PARAMETER_NAME);

        //最终生成的类文件名
        String finalClassName = Constants.GROUP_FILE_NAME + mModuleName;
        mMessager.printMessage(Diagnostic.Kind.NOTE, "APT生成路由组Group类文件：" +
                mPackageNameForAPT + "." + finalClassName);

        //生成类文件：HRouter$$Group$$app
        JavaFile javaFile = JavaFile.builder(mPackageNameForAPT, //包名
                TypeSpec.classBuilder(finalClassName) //类名
                        .addSuperinterface(ClassName.get(groupLoadType)) //实现HRouterLoadGroup接口
                        .addModifiers(Modifier.PUBLIC) //public 修饰符
                        .addMethod(methodBuilder.build())  //方法的构建
                        .build()) //类构建完成
                .build();
        javaFile.writeTo(mFiler);
    }

    /**
     * 赋值临时map存储，用来存放路由组group对应的详细path类对象，生成路由路径文件时遍历
     *
     * @param routerBean 路由详细信息，最终实体封装类
     */
    private void valueOfPathMap(RouterBean routerBean) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, "RouterBean >> " + routerBean.toString());
        if (checkRouterPath(routerBean)) {
            //开始赋值
            List<RouterBean> routerBeans = mTempPathMap.get(routerBean.getGroup());
            if (EmptyUtils.isEmpty(routerBeans)) {
                //如果这个组不存在，则创建
                routerBeans = new ArrayList<>();
                routerBeans.add(routerBean);
                mTempPathMap.put(routerBean.getGroup(), routerBeans);
            } else {
                //找到了key,直接加入临时集合
                routerBeans.add(routerBean);
            }
        } else {
            mMessager.printMessage(Diagnostic.Kind.ERROR, "@HRouter注解未按照规范，如 /app/MainActivity");
        }

    }

    /**
     * 校验@HRouter注解的值， 如果group未填写就从必填项path中截取数据
     *
     * @param routerBean
     * @return
     */
    private boolean checkRouterPath(RouterBean routerBean) {
        String group = routerBean.getGroup();
        String path = routerBean.getPath();
        // @HRouter注解的path值，必须要以 / 开头（模仿阿里ARouter路由架构）
        if (EmptyUtils.isEmpty(path) || !path.startsWith("/")) {
            return false;
        }
        //比如开发者代码为： path = "/MainActivity"  (即最后一个/在0位置)
        if (path.lastIndexOf("/") == 0) {
            return false;
        }
        //从第一个 / 到第二个/ 中间截取组名
        String finalGroup = path.substring(1, path.indexOf("/", 1));
        if (EmptyUtils.isEmpty(finalGroup)) {
            return false;
        }
        //@HRouter注解中有group赋值
        if (!EmptyUtils.isEmpty(group) && !group.equals(mModuleName)) {
            mMessager.printMessage(Diagnostic.Kind.ERROR, "@HRouter注解的group值必须和当前模块名相同");
            return false;
        } else {
            routerBean.setGroup(finalGroup);
        }
        return true;
    }
}
