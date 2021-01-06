/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.aop.eova;

import com.eova.config.EovaConst;
import com.eova.engine.EovaExp;
import com.eova.model.Menu;
import com.eova.model.MetaObject;
import com.eova.model.User;
import com.jfinal.core.Controller;

/**
 * Eova全局业务拦截器上下文
 *
 * @author Jieven
 * @date 2014-8-29
 */
public class EovaContext {

    /**
     * 当前控制器
     */
    public Controller ctrl;

    /**
     * 当前用户对象
     */
    public User user;

    /**
     * 当前菜单
     */
    public Menu menu;

    /**
     * 当前元对象
     * 元字段=object.fields
     */
    public MetaObject object;

    /**
     * 当前操作表达式
     */
    public EovaExp exp;

    public EovaContext(Controller ctrl) {
        this.ctrl = ctrl;
        this.user = ctrl.getSessionAttr(EovaConst.USER);
    }

    public int UID() {
        return this.user.get("id");
    }
}