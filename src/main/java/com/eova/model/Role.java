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
 * 用户角色
 *
 * @author Jieven
 * @date 2014-9-10
 */
public class Role extends BaseModel<Role> {

    public static final Role dao = new Role();
    private static final long serialVersionUID = -1794335434198017392L;

    /**
     * 获取下级角色
     *
     * @param lv
     * @return
     */
    public List<Role> findSubRole(int lv) {
        return this.find("select * from eova_role where lv > ?", lv);
    }

}