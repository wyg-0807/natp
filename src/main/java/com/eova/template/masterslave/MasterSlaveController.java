/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.template.masterslave;

import com.eova.config.EovaConst;
import com.eova.core.menu.config.MenuConfig;
import com.eova.model.*;
import com.jfinal.core.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 业务模版：主子表(Grid ref Grid)
 *
 * @author Jieven
 */
public class MasterSlaveController extends Controller {

    public void list() {

        String menuCode = this.getPara(0);

        Menu menu = Menu.dao.findByCode(menuCode);
        setAttr("menu", menu);

        MenuConfig config = menu.getConfig();
        String objectCode = config.getObjectCode();

        // 获取子对象集
        List<MetaObject> objects = new ArrayList<MetaObject>();
        List<String> objectCodes = config.getObjects();
        for (String code : objectCodes) {
            objects.add(MetaObject.dao.getByCode(code));
        }
        // 主->多子 元对象
        setAttr("object", MetaObject.dao.getByCode(objectCode));
        setAttr("objects", objects);

        // 用于页面逻辑
        setAttr("config", config);
        // json 配置 供Grid内的js 使用
        setAttr("configJson", menu.getStr("config"));

        // 是否存在查询条件
        setAttr("isQuery", MetaObject.dao.isExistQuery(objectCode));
        List<MetaField> fields = MetaField.dao.queryByObjectCode(objectCode);
        setAttr("itemList", fields);

        // 根据权限获取功能按钮
        User user = this.getSessionAttr(EovaConst.USER);
        List<Button> btnList = Button.dao.queryByMenuCode(menuCode, user.getRid());

        HashMap<Integer, List<Button>> btnMap = new HashMap<Integer, List<Button>>();
        for (Button b : btnList) {
            int group = b.getInt("group_num");
            List<Button> list = btnMap.get(group);
            if (list == null) {
                list = new ArrayList<Button>();
                btnMap.put(group, list);
            }
            list.add(b);
        }

        setAttr("btnMap", btnMap);

        render("/eova/template/masterslave/list.html");
    }

}