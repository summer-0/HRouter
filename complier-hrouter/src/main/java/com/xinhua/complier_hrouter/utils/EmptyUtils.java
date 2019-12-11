package com.xinhua.complier_hrouter.utils;

import java.util.Collection;
import java.util.Map;

/**
 * Created by 49944
 * Time: 2019/12/9 12:29
 * Des:
 */
public class EmptyUtils {
    public static boolean isEmpty(CharSequence c) {
        return c == null || c.length() == 0;
    }

    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
}
