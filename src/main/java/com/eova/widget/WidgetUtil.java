/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.widget;

import com.alibaba.fastjson.JSONObject;
import com.eova.common.utils.xx;
import com.eova.core.menu.config.TreeConfig;
import com.eova.model.Menu;
import com.eova.model.MetaField;
import com.eova.widget.tree.TreeUtil;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

import java.util.*;

public class WidgetUtil {

    /**
     * 原值被自动转成了中文显示，所以转换前先备份值为"字段_val"
     *
     * @param reList Grid数据集
     * @param pkName 当前Eova_Object的主键名
     */
    public static void copyValueColumn(List<Record> reList, String pkName, List<MetaField> eis) {
        // 复制主键列
        for (Record re : reList) {
            for (MetaField x : eis) {
                // 如果有表达式，说明会被翻译，所以需要备份列
                String exp = x.getStr("exp");
                if (!xx.isEmpty(exp)) {
                    String en = x.getEn();
                    // 备份被转换的列的值
                    re.set(en + "_val", re.get(en, ""));
                }
            }
            // 复制主键值列
            if (!xx.isEmpty(pkName)) {
                re.set("pk_val", re.get(pkName));
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public static List<Record> modelsToRecords(List<? extends Model> models) {
        List<Record> es = new ArrayList<>();
        for (Model m : models) {
            es.add(new Record().setColumns(m));
        }
        return es;
    }

    /**
     * Tree数据准备 List转有序Map
     *
     * @param records
     * @return
     */
    public static Map<String, Record> listToLinkedMap(List<Record> records, TreeConfig treeConfig) {
        LinkedHashMap<String, Record> temp = new LinkedHashMap<>();
        for (Record x : records) {
            // 获取EasyUI所需ICON字段名
            String icon = x.get("icon");
            if (xx.isEmpty(icon)) {
                icon = "icon-application";
            }
            x.set("icon", icon);

            temp.put(x.get(treeConfig.getIdField()).toString(), x);
        }
        return temp;
    }

    /**
     * Tree数据准备 List转有序Map
     *
     * @param records
     * @return
     */
    public static LinkedHashMap<Integer, Menu> menusToLinkedMap(List<Menu> records) {
        LinkedHashMap<Integer, Menu> temp = new LinkedHashMap<Integer, Menu>();
        for (Menu x : records) {
            // 获取EasyUI所需ICON字段名
            String icon = x.get("iconskip");
            if (xx.isEmpty(icon)) {
                icon = "icon-application";
            }
            x.put("icon", icon);

            temp.put(x.getInt("id"), x);
        }
        return temp;
    }

    /**
     * 将Menu转为Tree Json
     *
     * @param temp
     * @return
     */
    public static String menu2TreeJson(Map<Integer, Menu> temp, Integer rootId) {
        LinkedHashMap<String, Record> recordMap = new LinkedHashMap<String, Record>();
        // model to record
        for (Map.Entry<Integer, Menu> map : temp.entrySet()) {
            recordMap.put(map.getKey().toString(), new Record().setColumns(map.getValue()));
        }
        return map2TreeJson(recordMap, rootId);
    }

    /**
     * 菜单转化为Tree所需JSON结构
     *
     * @param temp
     * @return
     */
    public static String map2TreeJson(Map<String, Record> temp, Integer rootId) {
        // EasyUI 公共数据格式处理
        for (Map.Entry<String, Record> map : temp.entrySet()) {
            Record x = map.getValue();

            // 是否默认折叠
            String state = "open";
            Boolean isOpen = x.getBoolean("open");
            if (!xx.isEmpty(isOpen) && !isOpen) {
                state = "closed";
            }
            x.set("state", state);
            x.set("iconCls", x.getStr("icon"));
            // 移除无用属性
            x.remove("open");
            x.remove("icon");
            x.remove("url");
            x.remove("diy_js");
            x.remove("config");
            x.remove("biz_intercept");
        }


        Record tree = TreeUtil.buildTree(rootId + "", "parent_id", "children", temp);

        // 组装Tree Json
        StringBuilder sb = new StringBuilder("[");
        // 获取根节点
        List<Record> childList = tree.get("children");
        for (Record x : childList) {
            sb.append(JsonKit.toJson(x));
            sb.append(",");
        }
        sb.delete(sb.length() - 1, sb.length());
        sb.append("]");
        //System.out.println(sb.toString());

        // 大小写与EasyUI兼容问题：
        String json = sb.toString();
        json = json.replaceAll("iconcls", "iconCls");

        return json;
    }

    /**
     * 数据集合转TreeMap结构
     *
     * @param list
     * @param rootValue
     * @param idField
     * @param parentField
     * @param childrenField
     * @return
     */
    public static Record listToTree(List<Record> list, String rootValue, String idField, String parentField, String childrenField) {
        LinkedHashMap<String, Record> temp = new LinkedHashMap<>();

        // 变换数据结构，方便遍历 list to map
        for (Record x : list) {
            temp.put(x.get(idField).toString(), x);
        }

        return TreeUtil.buildTree(rootValue, parentField, childrenField, temp);
    }

    /**
     * 格式化EasyUI Tree
     *
     * @param maps Tree Map
     */
    public static void formatEasyTree(Map<Integer, Menu> maps) {
        for (Map.Entry<Integer, Menu> map : maps.entrySet()) {
            Menu x = map.getValue();

            // 目录无URL，为功能构建URL
            JSONObject attrs = new JSONObject();
            if (!x.getStr("type").equals(Menu.TYPE_DIR)) {
                attrs.put("url", x.getUrl());
            }

            x.put("id", x.getInt("id"));
            x.put("text", x.getStr("name"));
            x.put("attributes", attrs);
            // menu.put("checked", false);
            x.remove("name");
        }
    }

    /**
     * 递归获得上级节点
     *
     * @param allMenu    所有菜单
     * @param authParent 需要获取上级的节点Map(找到的父节点也会放进来)
     * @param menu       当前节点
     */
    public static void getParent(HashMap<Integer, Menu> allMenu, HashMap<Integer, Menu> authParent, Menu menu) {
        // 获取上级父节点
        Integer pid = menu.getInt("parent_id");
        // 上级不存在 || =0，说明到了Root
        if (xx.isEmpty(pid) || pid == 0) {
            return;
        }
        Menu pm = allMenu.get(pid);
        authParent.put(pid, pm);
        // 递归上上级
        getParent(allMenu, authParent, pm);
    }


}