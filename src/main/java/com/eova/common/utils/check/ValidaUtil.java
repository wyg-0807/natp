/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.common.utils.check;

import com.eova.common.utils.util.RegexUtil;

public class ValidaUtil {
    private static final String regex = "[$^&<>'/]";

    /**
     * 是否安全字符串(不包含$^&<>'/)
     *
     * @param str
     * @return
     */
    public static boolean isSecStr(String str) {
        return !RegexUtil.isExist(regex, str);
    }
}