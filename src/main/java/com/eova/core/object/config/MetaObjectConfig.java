/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.core.object.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.eova.common.utils.xx;

import java.util.LinkedHashMap;

/**
 * 元对象配置
 *
 * @author Jieven
 */
public class MetaObjectConfig {

    // 视图配置
    private LinkedHashMap<String, TableConfig> view;

    public MetaObjectConfig() {
    }

    public MetaObjectConfig(String s) {
        JSONObject json = JSON.parseObject(s);
        this.view = JSON.parseObject(json.getString("view"), new TypeReference<LinkedHashMap<String, TableConfig>>() {
        });
    }

    public static void main(String[] args) {
        MetaObjectConfig o = new MetaObjectConfig();
        // {"viewMap":{"users":{"key":"id","value":"id"},"users_exp":{"key":"users_id","value":"id"}}}
        LinkedHashMap<String, TableConfig> v = new LinkedHashMap<>();
        {
            TableConfig tc = new TableConfig();
            tc.setWhereField("id");
            tc.setParamField("id");
            v.put("users", tc);
        }
        {
            TableConfig tc = new TableConfig();
            tc.setWhereField("users_id");
            tc.setParamField("id");
            v.put("users_exp", tc);
        }
        o.setView(v);

        String s = JSONObject.toJSONString(o);
        System.out.println(xx.formatJson(s));

        MetaObjectConfig metaObjectConfig = new MetaObjectConfig(s);
        String s1 = JSONObject.toJSONString(metaObjectConfig);
        System.out.println(xx.formatJson(s1));

    }

    public LinkedHashMap<String, TableConfig> getView() {
        return view;
    }

    public void setView(LinkedHashMap<String, TableConfig> view) {
        this.view = view;
    }


}