/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.template.office;

import com.jfinal.core.Controller;

import java.util.HashMap;

public class OfficeIntercept {

    protected final static char Y = '☑';
    protected final static char N = '□';

    /**
     * 初始化获取模版数据
     *
     * @param data
     * @throws Exception
     */
    public void init(Controller ctrl, HashMap<String, Object> data) throws Exception {
    }

}