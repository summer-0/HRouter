package com.xinhua.hrouter_api.core;

import com.xinhua.annotation_hrouter.model.RouterBean;

import java.util.Map;

/**
 * Created by 49944
 * Time: 2019/12/9 10:42
 * Des: 路由组Group对应的详细Path加载数据接口
 * 如：app分组对应有哪些类需要加载
 */
public interface HRouterLoadPath {
    /**
     * 加载路由组Group中的Path详细数据
     * 如： "app" 分组下有这些信息：
     * @return key: "app/MainActivity"  value: MainActivity 信息封装到RouterBean对象中
     */
    Map<String, RouterBean> loadPath();
}
