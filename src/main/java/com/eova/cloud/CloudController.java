/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.cloud;

import com.jfinal.core.Controller;

/**
 * Eova云
 *
 * @author Jieven
 * @date 2015-1-6
 */
public class CloudController extends Controller {

    public void index() {
        render("/eova/cloud/index.html");
    }

}