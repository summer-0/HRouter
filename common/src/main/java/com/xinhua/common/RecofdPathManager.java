package com.xinhua.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 49944
 * Time: 2019/12/7 17:10
 * Des: 全局路径记录器（根据子模块分组）
 */
public class RecofdPathManager {
    // 对应着  <组  每个组的pathBean>  如: order组  order组中所有的pathBean
    //key: "order"组  value: order子模块下，对应所有的Activity路径信息
    private static Map<String, List<PathBean>> mGroupMap = new HashMap<>();

    /**
     * 将路径信息加入全局map
     *
     * @param group
     * @param pathName
     * @param clazz
     */
    public static void joinGroup(String group, String pathName, Class<?> clazz) {
        List<PathBean> list = mGroupMap.get(group);
        if (list == null) {
            //"order"组不存在
            list = new ArrayList<>();
            list.add(new PathBean(pathName, clazz));
            mGroupMap.put(group, list);
        } else {
            //"order"组存在
            //看是否之前添加过 若已经添加过，则不再添加
            for (PathBean pathBean : list) {
                if (!pathBean.getPath().equals(pathName)) {
                    list.add(new PathBean(pathName, clazz));
                }
            }
        }
    }

    /**
     * 根据组名和路径名获取类对象，达到跳转的目的
     *
     * @param group
     * @param pathName
     * @return
     */
    public static Class<?> getTargetClass(String group, String pathName) {
        List<PathBean> list = mGroupMap.get(group);
        if (list == null) {
            return null;
        }
        for (PathBean pathBean : list) {
            if (pathName.equalsIgnoreCase(pathBean.getPath())) {
                return pathBean.getClazz();
            }
        }
        return null;
    }
}
