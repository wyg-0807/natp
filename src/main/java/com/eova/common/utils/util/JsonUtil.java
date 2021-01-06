/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.common.utils.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonUtil {

    /**
     * JSON -> List&lt;Record>
     *
     * @param json
     * @return
     */
    public static List<Record> getRecords(String json) {
        List<Record> records = new ArrayList<Record>();

        List<JSONObject> list = JSON.parseArray(json, JSONObject.class);
        for (JSONObject o : list) {
            Map<String, Object> map = JSON.parseObject(o + "", new TypeReference<Map<String, Object>>() {
            });
            Record re = new Record();
            re.setColumns(map);
            records.add(re);
        }

        return records;
    }
}