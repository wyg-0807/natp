/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.common.utils.jfinal;

import com.eova.common.utils.xx;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

public class RecordUtil {

    /**
     * 从指定Record中剥离出指定字段
     *
     * @param data        原数据集
     * @param columnNames 要剥离的字段名组
     * @return 剥离出的数据集
     */
    public static Record peel(Record data, String... columnNames) {
        Record o = new Record();
        for (String name : columnNames) {
            if (name.contains("->")) {
                String[] ss = name.split("->");
                String source = ss[0].trim();
                String target = ss[1].trim();
                o.set(target, data.get(source));
                // 虚拟字段: Table模式不参与持久化无需移除, View模式也不应使用虚拟字段
                // 存在关系的字段不移除，后续其它表可能还需使用
            } else {
                o.set(name, data.get(name));
                data.remove(name);
            }
        }
        return o;
    }

    /**
     * 从指定Record中剥离出指定字段
     *
     * @param modelClass  类型
     * @param data        原数据集
     * @param columnNames 要剥离的字段名组(默认使用全部数据)
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T extends Model> T peelModel(Class<? extends Model> modelClass, Record data, String... columnNames) {
        Model<?> m = null;
        try {
            m = modelClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (xx.isEmpty(columnNames)) {
            m._setAttrs(data.getColumns());
        } else {
            Record r = peel(data, columnNames);
            m._setAttrs(r.getColumns());
        }
        return (T) m;
    }
}