package com.xinhua.complier_hrouter.utils;

/**
 * Created by 49944
 * Time: 2019/12/9 11:19
 * Des:
 */
public class Constants {
    //注解处理器支持的注解类型
    public static final String HROUTER_ANNOTATION_TYPES = "com.xinhua.annotation_hrouter.HRouter";

    //每个子模块的模块名
    public static final String MODULE_NAME = "moduleName";
    //用于存放apt生成的类文件
    public static final String APT_PACKAGE = "packageNameForAPT";

    //String全类名
    public static final String STRING = "java.lang.String";
    //Activity全类名
    public static final String ACTIVITY = "android.app.Activity";

    //包名前缀封装
    public static final String BASE_PACKAGE = "com.xinhua.hrouter_api";
    //路由组Group加载接口
    public static final String HROUTER_GROUP = BASE_PACKAGE + ".core.HRouterLoadGroup";
    //路由组Group对应的详细Path加载接口
    public static final String HROUTER_PATH = BASE_PACKAGE + ".core.HRouterLoadPath";

    //路由组Group对应的详细Path，方法名
    public static final String PATH_METHOD_NAME = "loadPath";
    //路由组Group对应的详细Path,参数名
    public static final String PATH_PARAMETER_NAME = "pathMap";
    //APT生成的路由组Group对应的详细Path类文件名
    public static final String PATH_FILE_NAME = "HRouter$$Path$$";

    //路由组Group，方法名
    public static final String GROUP_METHOD_NAME = "loadGroup";
    //路由组Group,参数名
    public static final String GROUP_PARAMETER_NAME = "groupMap";
    //APT生成的路由组Group名
    public static final String GROUP_FILE_NAME = "HRouter$$Group$$";
}
