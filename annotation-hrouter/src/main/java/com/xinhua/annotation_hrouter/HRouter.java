package com.xinhua.annotation_hrouter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by 49944
 * Time: 2019/12/8 1:13
 * Des:
 *
 * @Target(ElementType.TYPE) //接口、类、枚举、注解
 * @Target(ElementType.FIELD) //属性、枚举的常量
 * @Target(ElementType.METHOD) //方法
 * @Target(ElementType.PARAMETER) //方法参数
 * @Target(ElementType.CONSTRUCTOR) //构造函数
 * @Target(ElementType.LOCAL_VARIABLE) //局部变量
 * @Target(ElementType.ANNOTATION_TYPE) //该注解使用在另一个注解上
 * @Target(ElementType.PACKAGE) //包
 * @Retention(RetentionPolicy.RUNTIME) //注解会在class字节码文件中存在，jvm加载时可以通过反射获取到注解的内存
 * <p>
 * 生命周期：SOURCE < CLASS < RUNTIME
 * 1. 做一些检查性操作，如@Override，用SOURCE源码注解，注解仅存在源码级别，在编译的时候丢弃该注解
 * 2. 要在编译时进行一些预处理操作，如ButterKnife，用CLASS注解，注解会在CLASS文件中存在，但是会运行时丢弃
 * 3. 一般如果需要在运行时去动态获取注解信息，用RUNTIME注解（如运行时根据注解来生成一些类）
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface HRouter {
    //详细路由路径（必填） 如："app/MainActivity"  "order/Order_MainActivity"
    String path();
    //从path中截取出来，规范开发者的编码   (非必填)
    String group() default "";
}
