/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.template.treetogrid;

import com.eova.config.EovaConst;
import com.eova.core.menu.config.MenuConfig;
import com.eova.model.Button;
import com.eova.model.Menu;
import com.eova.model.MetaObject;
import com.eova.model.User;
import com.jfinal.core.Controller;

import java.util.List;

/**
 * 业务模版:树(Tree)关联表(Grid)
 *
 * @author Jieven
 */
public class TreeToGridController extends Controller {

    public void list() {

        String menuCode = this.getPara(0);

        // 获取元数据
        Menu menu = Menu.dao.findByCode(menuCode);
        MenuConfig config = menu.getConfig();
        String objectCode = config.getObjectCode();
        MetaObject object = MetaObject.dao.getByCode(objectCode);
        if (object == null) {
            throw new RuntimeException("元对象不存在,请检查:" + objectCode);
        }

        // 根据权限获取功能按钮
        User user = this.getSessionAttr(EovaConst.USER);
        List<Button> btnList = Button.dao.queryByMenuCode(menuCode, user.getRid());

        // 是否需要显示快速查询
        setAttr("isQuery", MetaObject.dao.isExistQuery(objectCode));

        setAttr("menu", menu);
        setAttr("btnList", btnList);
        setAttr("object", object);

        render("/eova/template/treetogrid/list.html");
    }

}