/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.engine;

/**
 * Eova表达式系统参数
 *
 * @author Jieven
 */
public enum EovaExpParam {
    CNAME("cname", "", "文本字段名"), // V1.6.1 Add 使用场景:订单编码
    ROOT("root", "0", "下拉树根节点的值"), // V1.6.0 Add 使用场景:下拉树指定根节点
    CACHE("cache", "", "缓存KEY"),
    DS("ds", "main", "数据源KEY");

    private final String val;// 参数Key
    private final String txt;// 参数描述
    private String def;// 参数默认值

    EovaExpParam(String val, String def, String txt) {
        this.val = val;
        this.def = def;
        this.txt = txt;
    }

    public String getVal() {
        return val;
    }

    public String getTxt() {
        return txt;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }

}