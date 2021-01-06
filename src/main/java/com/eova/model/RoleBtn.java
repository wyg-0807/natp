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
import com.eova.common.utils.xx;
import com.jfinal.plugin.activerecord.Db;

import java.util.List;

/**
 * 角色已授权功能点
 *
 * @author Jieven
 * @date 2014-9-10
 */
public class RoleBtn extends BaseModel<RoleBtn> {

    public static final RoleBtn dao = new RoleBtn();
    private static final long serialVersionUID = -1794335434198017392L;

    /**
     * 获取角色已授权功能(按钮)ID
     *
     * @param rid 角色ID
     * @return
     */
    public List<Integer> queryByRid(int rid) {
        return Db.use(xx.DS_EOVA).query("select bid from eova_role_btn where rid = ?", rid);
    }

    /**
     * 获取角色已授权功能
     *
     * @param rid 角色ID
     * @return
     */
    public List<RoleBtn> findByRid(String rids) {
        return this.find("select * from eova_role_btn where rid in (?)", rids);
    }

    /**
     * 删除菜单功能关联的权限
     *
     * @param menuCode
     */
    public void deleteByMenuCode(String menuCode) {
        String sql = "delete from eova_role_btn where bid in (select id from eova_button where menu_code = ?)";
        Db.use(xx.DS_EOVA).update(sql, menuCode);
    }

    /**
     * 删除按钮相关的权限
     *
     * @param bid 按钮ID
     */
    public void deleteByBid(int bid) {
        String sql = "delete from eova_role_btn where bid = ?";
        Db.use(xx.DS_EOVA).update(sql, bid);
    }

}