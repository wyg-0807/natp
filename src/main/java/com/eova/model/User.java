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
import com.eova.config.EovaConst;

public class User extends BaseModel<User> {

    public static final User dao = new User();
    private static final long serialVersionUID = 1064291771401662738L;
    public Role role;

    public int getRid() {
        return this.getInt("rid");
    }

    /**
     * 是否超级管理员
     *
     * @return
     */
    public boolean isAdmin() {
        return getIsAdmin();
    }

    // 为兼容模版取值
    public boolean getIsAdmin() {
        return this.getRid() == EovaConst.ADMIN_RID;
    }

    public void init() {
        this.role = Role.dao.findById(this.getInt("rid"));
    }

    public User getByLoginId(String loginId) {
        return this.findFirst("select * from eova_user where login_id = ?", loginId);
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

}