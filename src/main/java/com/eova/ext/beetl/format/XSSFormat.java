/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.ext.beetl.format;

import com.eova.common.utils.web.HtmlUtil;
import org.beetl.core.Format;

public class XSSFormat implements Format {

    @Override
    public Object format(Object data, String pattern) {
        if (null == data) {
            return null;
        } else {
            String content = (String) data;
            // XSS简单过滤
            content = HtmlUtil.XSSEncode(content);
            return content;
        }
    }
}