package com.xinhua.hrouter_api.core;

import java.util.Map;

/**
 * Created by 49944
 * Time: 2019/12/9 10:37
 * Des: 路由组Group对外提供加载数据接口
 */
public interface HRouterLoadGroup {
    /**
     * 加载路由组Group数据
     * 比如："app", HRouter$$Path$$app.class (实现了HRouterLoadPath接口)
     * @return key: "app", value: "app" 分组对应的路由详细对象类
     */
    Map<String, Class<? extends HRouterLoadPath>> loadGroup();
}
