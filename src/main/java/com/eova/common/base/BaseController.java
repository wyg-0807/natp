/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.common.base;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eova.common.utils.xx;
import com.eova.config.EovaConst;
import com.eova.model.User;
import com.jfinal.core.Controller;
import com.jfinal.json.Json;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Model;

import java.util.HashSet;

public class BaseController extends Controller {

    protected String http(String domain) {
        return "http://" + xx.getConfig(domain);
    }

    protected int UID() {
        return getUser().getInt("id");
    }

    protected int RID() {
        return getUser().getInt("rid");
    }

    protected User getUser() {
        return getSessionAttr(EovaConst.USER);
    }

    /**
     * <pre>
     * 获取选中行某列的值
     * 多选且为整型:1,2,3
     * 多选且为字符:'a','b','c'
     * PS:多选的处理方便取值后直接in
     * </pre>
     *
     * @param field 字段名
     */
    protected String getSelectValue(String field) {
        // 单选
        JSONArray rows = getSelectRows();
        if (rows.isEmpty()) {
            return null;
        }
        if (rows.size() == 1) {
            return rows.getJSONObject(0).getString(field);
        }

        boolean isNum = true;
        // 多选
        HashSet<String> set = new HashSet<>();
        for (int i = 0; i < rows.size(); i++) {
            Object val = rows.getJSONObject(i).get(field);
            set.add(val.toString());
            if (!xx.isNum(val))
                isNum = false;
        }
        if (isNum) {
            return xx.join(set);
        } else {
            return xx.join(set, "'", ",");
        }
    }

    protected String getSelectValue(String field, String def) {
        String val = getSelectValue(field);
        return xx.isEmpty(val) ? def : val;
    }

    protected Integer getSelectValueToInt(String field) {
        return xx.toInt(getSelectValue(field));
    }

    /**
     * 获取所有选中行
     *
     * @return
     */
    protected JSONArray getSelectRows() {
        String json = getPara("rows");
        JSONArray o = JSONObject.parseArray(json);
        return o;
    }

    /**
     * 获取按钮输入框的值
     * <pre>
     * Integer id = getSelectValue("id");
     *
     * if (xx.isEmpty(val)) {
     *     renderJson(new Easy("参数不能为空！"));
     *     return;
     * }
     *
     * Db.update("update xxx set xx = ? where id = ?", val, id);
     * renderJson(Easy.sucess());
     * </pre>
     */
    protected String getInputValue() {
        return getPara("input");
    }

    /**
     * 获取JSONObject
     */
    protected Ret getJsonToRet() {
        String s = HttpKit.readData(getRequest());
        if (xx.isEmpty(s)) {
            return null;
        }
        return Json.getJson().parse(s, Ret.class);
        // return JSONObject.parseObject(HttpKit.readData(getRequest()));
    }

    /**
     * 快速GET Request请求参数
     *
     * @param modelClass
     * @param fields
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected <T extends Model> T getModel(Class<? extends Model> modelClass, String... fields) {
        Model<?> m = null;
        try {
            m = modelClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String field : fields) {
            if (!field.contains(" ")) {
                m.set(field, getPara(field));
                continue;
            }
            String[] ss = field.split("\\s+");
            String type = ss[0];
            String col = ss[1];
            Object val = "";
            if (type.equals("int")) {
                val = getParaToInt(col);
            } else if (type.equals("long")) {
                val = getParaToLong(col);
            } else if (type.equals("double")) {
                val = xx.toDouble(getPara());
            } else if (type.equals("boolean")) {
                val = getParaToBoolean(col);
            } else if (type.equals("date")) {
                val = getParaToDate(col);
            } else {
                val = getPara(col);
            }
            m.set(col, val);
        }

        return (T) m;
    }
}