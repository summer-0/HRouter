package com.xinhua.hrouter.test;

import com.xinhua.annotation_hrouter.model.RouterBean;
import com.xinhua.hrouter_api.core.HRouterLoadPath;
import com.xinhua.order.Order_MainActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 49944
 * Time: 2019/12/9 11:23
 * Des: 模块HRouter路由组文件，对应的路径
 */
public class HRouter$$Path$$Order implements HRouterLoadPath {
    @Override
    public Map<String, RouterBean> loadPath() {
        Map<String, RouterBean> pathMap = new HashMap<>();

        pathMap.put("order/Order_MainActivity",
                RouterBean.create(RouterBean.Type.ACTIVITY,
                        Order_MainActivity.class,
                        "order/OrderMainActivity",
                        "order"));
        pathMap.put("order/Order_DetailActivity",
                RouterBean.create(RouterBean.Type.ACTIVITY,
                        Order_MainActivity.class,
                        "order/OrderDetailActivity",
                        "order"));

        return pathMap;
    }
}
