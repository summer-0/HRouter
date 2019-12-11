package com.xinhua.hrouter.test;

import com.xinhua.hrouter_api.core.HRouterLoadGroup;
import com.xinhua.hrouter_api.core.HRouterLoadPath;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 49944
 * Time: 2019/12/9 11:23
 * Des: 模块HRouter路径组
 */
public class HRouter$$Group$$Order implements HRouterLoadGroup {
    @Override
    public Map<String, Class<? extends HRouterLoadPath>> loadGroup() {
        Map<String, Class<? extends HRouterLoadPath>> groupMap = new HashMap<>();
        groupMap.put("order", HRouter$$Path$$Order.class);
        return groupMap;
    }
}
