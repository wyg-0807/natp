/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.core.menu;

import com.eova.aop.AopContext;
import com.eova.aop.MetaObjectIntercept;
import com.eova.common.base.BaseCache;
import com.eova.config.EovaConst;
import com.eova.model.Button;
import com.eova.model.Menu;
import com.eova.model.RoleBtn;

public class MenuIntercept extends MetaObjectIntercept {

    @Override
    public String hideBefore(AopContext ac) throws Exception {
        System.out.println("隐藏菜单:" + ac.record.getInt("id"));
        return null;
    }

    @Override
    public String deleteBefore(AopContext ac) throws Exception {
        int id = ac.record.getInt("id");
        Menu menu = Menu.dao.findById(id);
        String code = menu.getStr("code");

        // 有子菜单禁止删除
        //		boolean isPaent = Menu.dao.isParent(id);
        //		if (isPaent) {
        //			// 开发小常识：有子的父不能删！
        //			return error("如果爹没了，仔仔们会很伤心的！");
        //		}

        // 删除菜单按钮关联权限
        RoleBtn.dao.deleteByMenuCode(code);

        // 删除菜单关联按钮
        Button.dao.deleteByMenuCode(code);

        // 删除菜单关联对象,不能删除对象，因为对象可能被多个菜单用
        // MenuObject.dao.deleteByMenuCode(code);

        return null;
    }

    @Override
    public String addSucceed(AopContext ac) throws Exception {
        // 菜单使缓存失效
        BaseCache.delSer(EovaConst.ALL_MENU);

        return null;
    }

    @Override
    public String deleteSucceed(AopContext ac) throws Exception {
        // 菜单使缓存失效
        BaseCache.delSer(EovaConst.ALL_MENU);

        return null;
    }

    @Override
    public String updateSucceed(AopContext ac) throws Exception {
        // 菜单使缓存失效
        BaseCache.delSer(EovaConst.ALL_MENU);

        return null;
    }

}