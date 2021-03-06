/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.cloud;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eova.cloud.auth.EovaApp;
import com.eova.common.utils.HttpUtils;
import com.eova.common.utils.util.JsonUtil;
import com.eova.common.utils.xx;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

public class EovaCloud {

    /**
     * 云端人工智能预处理元数据
     *
     * @param objectCode 元对象编码
     */
    public static void buildMeta(final String objectCode) {
        if (!xx.getConfigBool("cloud_service", false)) {
            return;
        }
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    List<Record> list = Db.use(xx.DS_EOVA).find("select * from eova_field where object_code = ?", objectCode);

                    JSONObject json = new JSONObject();
                    json.put("app_id", xx.getConfig("app_id"));
                    json.put("app_secret", xx.getConfig("app_secret"));
                    json.put("data", JsonKit.toJson(list));

                    String url = "http://www.eova.cn/api/meta";
                    String result = HttpUtils.cs().postJson(url, json.toJSONString());
                    if (!xx.isEmpty(result)) {
                        List<Record> records = JsonUtil.getRecords(result);
                        Db.use(xx.DS_EOVA).batchUpdate("eova_field", records, records.size());
                    }
                } catch (Exception e) {
                    LogKit.info("eova cloud meta build exception：" + e.getMessage());
                }
            }
        };
        t.start();
    }

    public static void app() {
        final EovaApp app = AuthCloud.getEovaApp();
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    HttpUtils.cs().postJson("http://www.eova.cn/api/app", JSON.toJSONString(app));
                } catch (Exception e) {
                    LogKit.debug("eova app update error：" + e.getMessage());
                }
            }
        };
        t.start();
    }

}