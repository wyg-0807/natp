/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.template;

import com.eova.model.Button;

import java.util.List;
import java.util.Map;

/**
 * Eova 业务模版接口
 *
 * @author Jieven
 */
public interface Template {
    /**
     * 模版名称
     *
     * @return
     */
    String name();

    /**
     * 模版编码
     *
     * @return
     */
    String code();

    /**
     * 模版按钮组
     *
     * @return
     */
    Map<Integer, List<Button>> getBtnMap();

}