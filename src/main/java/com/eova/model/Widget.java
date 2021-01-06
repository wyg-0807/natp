/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.model;

import com.eova.common.base.BaseModel;

import java.util.List;

/**
 * 控件
 *
 * @author Jieven
 * @date 2014-9-10
 */
public class Widget extends BaseModel<Widget> {

    public static final Widget dao = new Widget();
    /**
     * EOVA控件
     **/
    public static final int TYPE_EOVA = 1;
    /**
     * DIY控件
     **/
    public static final int TYPE_DIY = 2;
    private static final long serialVersionUID = 4254060861819273244L;

    public List<Widget> findByType(int type) {
        return this.queryByCache("select * from eova_widget where type = ?", type);
    }
}