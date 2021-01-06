/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.core.menu.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * 菜单配置
 *
 * @author Jieven
 */
public class MenuConfig {
    // 主对象Code
    private String objectCode;
    // 主对象外键字段
    private String objectField;

    // 子对象Code列表
    private List<String> objects;
    // 子对象关联字段列表
    private List<String> fields;

    // 树属性
    private TreeConfig tree;

    // 图属性
    private ChartConfig chart;

    // 个性化参数
    private JSONObject params = new JSONObject();

    public MenuConfig() {
    }

    public MenuConfig(String str) {
        JSONObject json = JSON.parseObject(str);
        this.objectCode = json.getString("objectCode");
        this.objectField = json.getString("objectField");
        this.objects = JSON.parseArray(json.getString("objects"), String.class);
        this.fields = JSON.parseArray(json.getString("fields"), String.class);
        this.tree = JSON.parseObject(json.getString("tree"), TreeConfig.class);
        this.chart = JSON.parseObject(json.getString("chart"), ChartConfig.class);

        this.params = JSON.parseObject(json.getString("params"));
    }

    public static void main(String[] args) {
        MenuConfig mc = new MenuConfig();
        TreeConfig tc = new TreeConfig();
        tc.setObjectCode("eova_menu_code");
        tc.setObjectField("code");

        tc.setIdField("id");
        tc.setParentField("parent_id");
        tc.setTreeField("name");
        tc.setIconField("iconskip");
        mc.setTree(tc);

        mc.setObjectCode("eova_button_code");
        mc.setObjectField("menu_code");

        JSONObject params = new JSONObject();
        params.put("slave_height", 500);
        mc.setParams(params);

        String s = JSONObject.toJSONString(mc);
        System.out.println(s);
    }

    public String getObjectCode() {
        return objectCode;
    }

    public void setObjectCode(String objectCode) {
        this.objectCode = objectCode;
    }

    public String getObjectField() {
        return objectField;
    }

    public void setObjectField(String objectField) {
        this.objectField = objectField;
    }

    public List<String> getObjects() {
        return objects;
    }

    public void setObjects(List<String> objects) {
        this.objects = objects;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public TreeConfig getTree() {
        return tree;
    }

    public void setTree(TreeConfig tree) {
        this.tree = tree;
    }

    public ChartConfig getChart() {
        return chart;
    }

    public void setChart(ChartConfig chart) {
        this.chart = chart;
    }

    public JSONObject getParams() {
        return params;
    }

    public void setParams(JSONObject params) {
        this.params = params;
    }

}