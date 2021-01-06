/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.i18n;

import com.eova.common.utils.xx;

import java.util.HashMap;

public class I18N extends HashMap<String, String> {

    private static final long serialVersionUID = 1L;

    public String get(String key) {
        String s = super.get(key);
        if (xx.isEmpty(s)) {
            return key;
        }
        return s;
    }

}