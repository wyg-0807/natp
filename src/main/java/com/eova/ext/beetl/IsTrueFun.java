/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.ext.beetl;

import org.beetl.core.Context;
import org.beetl.core.Function;

/**
 * 自定判断是否为True
 *
 * @author Jieven
 * @date 2014-5-23
 */
public class IsTrueFun implements Function {
    public Object call(Object[] paras, Context ctx) {
        if (paras.length != 1) {
            throw new RuntimeException("参数错误，期望Object");
        }
        Object para = paras[0];
        if (para == null) {
            return false;
        }
        if (para.toString().equalsIgnoreCase("true")) {
            return true;
        }
        return para.toString().equals("1");
    }
}