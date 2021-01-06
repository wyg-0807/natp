/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.common.base;

import com.eova.common.utils.web.HtmlUtil;

import java.util.Map;

public class BaseSharedMethod {

    private final Map<String, String> conf;

    public BaseSharedMethod(Map<String, String> conf) {
        this.conf = conf;
    }

    public String htt(String domain) {
        return "//" + conf.get(domain);
    }

    public String http(String domain) {
        return "http://" + conf.get(domain);
    }

    public String https(String domain) {
        return "https://" + conf.get(domain);
    }

    public String dir(String dirName) {
        return conf.get("dir.static") + conf.get("dir." + dirName);
    }

    //	 非法内容过滤,适用于纯文本输出
    public String xss(String s) {
        return HtmlUtil.XSSEncode(s);
    }

    // html内容转码
    public String html(String s) {
        return HtmlUtil.HTMLEncode(s);
    }
}