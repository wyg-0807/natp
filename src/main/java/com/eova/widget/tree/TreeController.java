/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.widget.tree;

import com.eova.aop.AopContext;
import com.eova.aop.MetaObjectIntercept;
import com.eova.common.utils.xx;
import com.eova.core.menu.config.TreeConfig;
import com.eova.model.Menu;
import com.eova.model.MetaObject;
import com.eova.service.sm;
import com.eova.template.common.util.TemplateUtil;
import com.eova.widget.WidgetManager;
import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Tree组件
 *
 * @author Jieven
 */
public class TreeController extends Controller {

    final Controller ctrl = this;

    /**
     * 元对象业务拦截器
     **/
    protected MetaObjectIntercept intercept = null;

    public void query() throws Exception {

        String objectCode = getPara(0);
        String menuCode = getPara(1);

        MetaObject object = sm.meta.getMeta(objectCode);
        Menu menu = Menu.dao.findByCode(menuCode);
        TreeConfig tc = menu.getConfig().getTree();

        intercept = TemplateUtil.initMetaObjectIntercept(object.getBizIntercept());

        // 构建查询
        List<Object> parmList = new ArrayList<Object>();
        String sql = WidgetManager.buildQuerySQL(ctrl, menu, object, intercept, parmList, false, "");

        // 默认查询所有字段(如果想显示图标字段必须叫iconskip)
        String selelct = "select *";
        // 如果存在配置仅查询配置项
        if (menu != null) {
            // 根据Tree配置构造查询项
            selelct = MessageFormat.format("select {0},{1},{2},{3}", tc.getObjectField(), tc.getIdField(), tc.getParentField(), tc.getTreeField());
            if (!xx.isEmpty(tc.getIconField())) {
                selelct += ',' + tc.getIconField();
            }
        } else {
            // TODO tree.tag 提交 必须字段,防止浪费查询性能
        }

        // 转换SQL参数
        Object[] paras = new Object[parmList.size()];
        parmList.toArray(paras);
        List<Record> list = Db.use(object.getDs()).find(selelct + " " + sql, paras);

        // 有条件时，自动反向查找父节点数据 (条件会导致父节点丢失)
        if (!xx.isEmpty(sql.toLowerCase().concat("where"))) {
            // 向上查找父节点数据
            WidgetManager.findParent(tc, object.getDs(), "select *", object.getView(), tc.getIdField(), list, list);
        }

        // 查询后置任务
        if (intercept != null) {
            AopContext ac = new AopContext(ctrl, list);
            ac.object = object;
            intercept.queryAfter(ac);
        }

        renderJson(JsonKit.toJson(list));
    }

}