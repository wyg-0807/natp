/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.common.utils.data;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {

    /**
     * List&lt;Object> 转常用数值类型
     *
     * @param list
     * @param cs
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> toNumber(List<Object> list, Class<? extends Number> cs) {
        if (list.isEmpty()) {
            return null;
        }
        List<Object> t = new ArrayList<>();
        for (Object o : list) {
            if (o == null)
                continue;
            String s = o.toString();
            if (cs == Integer.class) {
                t.add(Integer.valueOf(s));
            } else if (cs == Long.class) {
                t.add(Long.valueOf(s));
            } else if (cs == Float.class) {
                t.add(Float.valueOf(s));
            } else if (cs == Double.class) {
                t.add(Double.valueOf(s));
            }
        }
        return (List<T>) t;
    }

}