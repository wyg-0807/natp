/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.ext.beetl;

import com.eova.common.utils.xx;
import org.beetl.core.Context;
import org.beetl.core.Function;

/**
 * JS 参数自动获取并处理默认值
 *
 * @author Jieven
 * @date 2014-5-23
 */
public class JsFormatFun implements Function {
    @Override
    public Object call(Object[] paras, Context ctx) {
        if (paras.length != 1) {
            throw new RuntimeException("参数错误，期望Object");
        }
        Object obj = paras[0];
        if (xx.isEmpty(obj)) {
            return "undefined";
        }
        if (xx.isNum(obj)) {
            return obj.toString();
        }
        return xx.format(obj);
    }
}