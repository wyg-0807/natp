/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.widget.treegrid;

import com.eova.aop.AopContext;
import com.eova.aop.MetaObjectIntercept;
import com.eova.common.base.BaseController;
import com.eova.common.utils.xx;
import com.eova.core.menu.config.TreeConfig;
import com.eova.model.Menu;
import com.eova.model.MetaObject;
import com.eova.service.sm;
import com.eova.template.common.util.TemplateUtil;
import com.eova.widget.WidgetManager;
import com.eova.widget.WidgetUtil;
import com.eova.widget.tree.TreeUtil;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * TreeGrid组件
 *
 * @author Jieven
 */
public class TreeGridController extends BaseController {

    final Controller ctrl = this;

    /**
     * 元对象业务拦截器
     **/
    protected MetaObjectIntercept intercept = null;

    /**
     * 数据查询
     *
     * @throws Exception
     */
    public void query() throws Exception {

        String objectCode = getPara(0);
        String menuCode = getPara(1);

        MetaObject object = sm.meta.getMeta(objectCode);
        Menu menu = Menu.dao.findByCode(menuCode);
        TreeConfig tc = menu.getConfig().getTree();

        if (xx.isEmpty(object.getPk())) {
            throw new RuntimeException("TreeGrid模版使用的元对象必须设置主键");
        }

        intercept = TemplateUtil.initMetaObjectIntercept(object.getBizIntercept());

        // 构建查询
        List<Object> paras = new ArrayList<Object>();
        String select = "select " + WidgetManager.buildSelect(object, RID());
        String sql = WidgetManager.buildQuerySQL(ctrl, menu, object, intercept, paras, true, select);
        List<Record> list = Db.use(object.getDs()).find(select + " " + sql, xx.toArray(paras));

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

        // 备份Value列，然后将值列转换成Key列
        WidgetUtil.copyValueColumn(list, object.getPk(), object.getFields());
        // 根据表达式将数据中的值翻译成汉字
        WidgetManager.convertValueByExp(this, object.getFields(), list, tc.getIdField(), tc.getParentField());

        String json = "{}";
        if (!xx.isEmpty(list)) {
            json = TreeUtil.toTreeJson(list, tc);
        }

        renderJson(json);
    }

}