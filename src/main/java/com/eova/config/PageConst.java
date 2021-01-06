/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.config;

import com.eova.common.utils.xx;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 页面常量
 *
 * @author Jieven
 */
public class PageConst {

    /**
     * 表单名-当前页码
     **/
    public static final String PAGENUM = "page";
    /**
     * 表单名-显示数量
     **/
    public static final String PAGESIZE = xx.getConfig("ui", "layui").equals("layui") ? "limit" : "rows";
    /**
     * 表单名-表单排序Key
     **/
    public static final String SORT = xx.getConfig("ui", "layui").equals("layui") ? "field" : "sort";
    /**
     * 表单名-表单排序方式Key
     **/
    public static final String ORDER = "order";
    /**
     * 表单名-查询表单前缀
     **/
    public static final String QUERY = "query_";
    /**
     * 表单名-范围查询表单前缀
     **/
    public static final String START = "start_";
    /**
     * 表单名-范围查询表单前缀
     **/
    public static final String END = "end_";
    /**
     * 表单名-条件前缀
     **/
    public static final String COND = "condition_";

    /**
     * 表单名-元对象
     **/
    public static final String EOVA_CODE = "eova_code";
    /**
     * 表单名-元字段
     **/
    public static final String EOVA_FIELD = "eova_field";

    /**
     * 系统启动初始化加载 将常量全局化
     */
    public static void init(Map<String, Object> sharedVars) {
        // long time = System.currentTimeMillis();
        System.err.println("Load Page Const Starting:");
        Field[] fds = PageConst.class.getDeclaredFields();
        for (Field fd : fds) {
            String key = fd.getName();
            try {
                // 设置为FreeMarker全局变量
                // FreeMarkerRender.getConfiguration().setSharedVariable(key, fd.get(key).toString());
                // JFinal.me().getServletContext().setAttribute(key, fd.get(key).toString());
                // 设置全局变量
                sharedVars.put(key, fd.get(key).toString());
                //System.err.println(key + " = [" + fd.get(null).toString() + "]");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // System.err.println("Load Cost Time:" + (System.currentTimeMillis() - time) + "ms\n");
    }
}