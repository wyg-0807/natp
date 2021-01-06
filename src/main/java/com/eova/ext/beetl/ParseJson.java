/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.ext.beetl;

import com.alibaba.fastjson.JSONObject;
import org.beetl.core.Context;
import org.beetl.core.Function;

/**
 * JSON字符串转对象
 *
 * @author Jieven
 * @date 2014-5-23
 */
public class ParseJson implements Function {
    @Override
    public Object call(Object[] paras, Context ctx) {
        if (paras.length != 1) {
            throw new RuntimeException("参数错误，请传入一个JSON字符串");
        }
        Object para = paras[0];
        if (para == null) {
            return null;
        }
        return JSONObject.parse(para.toString());
    }
}