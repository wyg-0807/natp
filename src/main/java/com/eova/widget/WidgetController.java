/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.widget;

import com.alibaba.fastjson.JSONObject;
import com.eova.aop.eova.EovaContext;
import com.eova.common.Easy;
import com.eova.common.base.BaseController;
import com.eova.common.utils.db.SqlUtil;
import com.eova.common.utils.xx;
import com.eova.config.EovaConfig;
import com.eova.config.EovaConst;
import com.eova.config.PageConst;
import com.eova.core.menu.config.TreeConfig;
import com.eova.engine.DynamicParse;
import com.eova.engine.EovaExp;
import com.eova.engine.EovaExpParam;
import com.eova.i18n.I18NBuilder;
import com.eova.model.MetaField;
import com.eova.model.MetaObject;
import com.eova.model.User;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * EOVA 控件
 *
 * @author Jieven
 */
public class WidgetController extends BaseController {

    /**
     * 获取SQL参数，优先Integer，不能转就当String
     *
     * @param str
     * @return
     */
    private static Object getSqlParam(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return str;
        }
    }

    /**
     * 查找Dialog
     */
    public void toFind() {
        render("/eova/widget/find/find.html");
    }

    /**
     * 查找框Dialog
     */
    public void find() {

        String url = "";
        String exp = getPara("exp");
        String code = getPara(PageConst.EOVA_CODE);
        String field = getPara(PageConst.EOVA_FIELD);
        boolean isMultiple = getParaToBoolean("multiple", false);

        // 准备数据查询参数根据元字段获取
        if (!xx.isEmpty(field)) {
            url += String.format("%s=%s&%s=%s", PageConst.EOVA_CODE, code, PageConst.EOVA_FIELD, field);
        } else {
            url += "exp=" + exp;
        }

        // 构建表达式
        exp = buildExp(null, exp, code, field);

        // 根据表达式构建元数据
        EovaExp se = new EovaExp(exp);
        MetaObject mo = se.getObject();
        List<MetaField> mfs = se.getFields();
        I18NBuilder.models(mfs, "cn");
        if (isMultiple) {
            mo.set("is_single", false);
        }

        url = "/widget/findJson?" + url;
        setAttr("action", url);
        // 用于Grid呈现
        setAttr("objectJson", JsonKit.toJson(mo));
        setAttr("fieldsJson", JsonKit.toJson(mfs));
        // 用于query条件
        setAttr("itemList", mfs);
        setAttr("pk", se.pk);

        toFind();
    }

    /**
     * Find Dialog Grid Get JSON
     */
    public void findJson() {

        String exp = getPara("exp");
        String code = getPara(PageConst.EOVA_CODE);
        String field = getPara(PageConst.EOVA_FIELD);

        try {
            List<Object> paras = new ArrayList<Object>();

            // 构建表达式
            exp = buildExp(paras, exp, code, field);

            // 解析表达式
            EovaExp se = new EovaExp(exp);

            // 获取分页参数
            int pageNumber = getParaToInt(PageConst.PAGENUM, 1);
            int pageSize = getParaToInt(PageConst.PAGESIZE, 15);

            String sql = WidgetManager.buildExpSQL(this, se, paras);

            Page<Record> page = Db.use(se.ds).paginate(pageNumber, pageSize, se.simpleSelect, sql, xx.toArray(paras));

            I18NBuilder.records(page.getList(), se.cn);

            // 构建JSON数据
            String ui = xx.getConfig("ui", "easyui");
            if (ui.equals("easyui")) {
                ui = "{\"total\":%s,\"rows\": %s}";
            } else if (ui.equals("layui")) {
                ui = "{\"code\": 0, \"msg\": \"\", \"count\":\"%s\",\"data\": %s}";
            }

            renderJson(String.format(ui, page.getTotalRow(), JsonKit.toJson(page.getList())));
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(Easy.fail("查找框查询数据异常:" + e.getMessage()));
        }
    }

    /**
     * Find get CN by value
     */
    public void findCnByEn() {

        String value = getPara("val");

        String code = getPara(PageConst.EOVA_CODE);
        String field = getPara(PageConst.EOVA_FIELD);
        String exp = getPara("exp");

        List<Object> paras = new ArrayList<Object>();
        // 构建表达式
        exp = buildExp(paras, exp, code, field);

        // 解析表达式
        EovaExp se = new EovaExp(exp);
        String ds = se.ds;

        // 查询本次所有翻译值
        StringBuilder sb = new StringBuilder();
        if (!xx.isEmpty(value)) {
            sb.append(se.pk);
            sb.append(" in(");
            // 根据当前页数据value列查询外表name列
            for (String id : value.split(",")) {
                // TODO There might be a sb injection risk warning
                sb.append(xx.format(id)).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
        }
        // System.out.println(sb.toString());

        // 根据表达式查询获得翻译的值
        String sql = WidgetManager.addWhere(se, sb.toString());

        List<Record> txts = Db.use(ds).find(sql, xx.toArray(paras));
        // 没有翻译值，直接返回原值
        if (xx.isEmpty(txts)) {
            JSONObject json = new JSONObject();
            json.put("code", 0);
            json.put("data", value);
            renderJson(json.toJSONString());
            return;
        }

        I18NBuilder.records(txts, se.cn);

        JSONObject json = new JSONObject();
        json.put("code", 1);
        json.put("text_field", se.cn);// 文本字段名
        json.put("data", JsonKit.toJson(txts));// 翻译字典数据
        renderJson(json.toJSONString());
    }

    /**
     * Combo Load Data Get JSON
     */
    public void comboJson() {
        String objectCode = getPara(0);
        String en = getPara(1);
        String exp = getPara("exp");

        try {
            List<Object> paras = new ArrayList<Object>();
            // 构建表达式
            exp = buildExp(paras, exp, objectCode, en);

            // 解析表达式
            EovaExp se = new EovaExp(exp);

            // 全局数据拦截条件
            if (EovaConfig.getEovaIntercept() != null) {
                EovaContext ec = new EovaContext(this);
                ec.exp = se;

                // 获取全局拦截器表达式公共条件
                String condition = EovaConfig.getEovaIntercept().filterExp(ec);
                // 动态变更表达式条件
                se.where = SqlUtil.appendWhereCondition(se.where, condition);
            }
            // 获取表达式变更后的最终SQL
            String sql = se.toString();

            // 缓存配置
            String cache = se.getPara(EovaExpParam.CACHE);
            List<Record> list = null;
            if (xx.isEmpty(cache)) {
                list = Db.use(se.ds).find(sql, xx.toArray(paras));
            } else {
                list = Db.use(se.ds).findByCache(cache, sql, sql, xx.toArray(paras));
            }

            I18NBuilder.records(list, "cn");

            renderJson(list);
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(Easy.fail("下拉框查询数据异常:" + e.getMessage()));
        }
    }

    /**
     * ComboTree Load Data Get JSON
     */
    public void comboTreeJson() {

        String objectCode = getPara(0);
        String en = getPara(1);
        String exp = getPara("exp");

        try {
            List<Object> paras = new ArrayList<Object>();

            // 构建表达式
            exp = buildExp(paras, exp, objectCode, en);

            // 解析表达式
            EovaExp se = new EovaExp(exp);

            // 全局数据拦截条件
            if (EovaConfig.getEovaIntercept() != null) {
                EovaContext ec = new EovaContext(this);
                ec.exp = se;

                // 获取全局拦截器表达式公共条件
                String condition = EovaConfig.getEovaIntercept().filterExp(ec);
                // 动态变更表达式条件
                se.where = SqlUtil.appendWhereCondition(se.where, condition);
            }
            // 获取表达式变更后的最终SQL
            String sql = se.toString();

            // 缓存配置
            String cache = se.getPara(EovaExpParam.CACHE);
            List<Record> list = null;
            if (xx.isEmpty(cache)) {
                list = Db.use(se.ds).find(sql, xx.toArray(paras));
            } else {
                list = Db.use(se.ds).findByCache(cache, sql, sql, xx.toArray(paras));
            }
            TreeConfig treeConfig = new TreeConfig();
            treeConfig.setIdField("id");
            treeConfig.setTreeField("name");
            treeConfig.setParentField("pid");
            treeConfig.setRootPid(se.getPara(EovaExpParam.ROOT));// 获取表达式自定义参数中rootid,默认为0
            // treeConfig.setIconField("icon"); TODO 暂不支持自定义Tree图标

            // 有条件时，自动方向查找父节点数据
            if (!xx.isEmpty(sql.toLowerCase().concat("where"))) {
                // 向上查找父节点数据
                WidgetManager.findParent(treeConfig, se.ds, se.select, se.table, se.pk, list, list);
            }
            renderJson(list);
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(Easy.fail("下拉树查询数据异常:" + e.getMessage()));
        }
    }

    /**
     * 获取表达式
     *
     * @param parmList   SQL动态参数
     * @param exp        自定义表达式
     * @param objectCode 元对象编码
     * @param field      元字段名
     * @return
     */
    private String buildExp(List<Object> parmList, String exp, String objectCode, String field) {
        if (xx.isEmpty(exp)) {
            // 获取元字段的表达式
            MetaField ei = MetaField.dao.getByObjectCodeAndEn(objectCode, field);
            exp = ei.getStr("exp");
        } else {
            exp = exp.trim();
            // 预处理表达式
            try {
                String[] strs = exp.split(",");
                if (strs.length > 0) {
                    exp = EovaConfig.getExps().get(strs[0]);
                    if (xx.isEmpty(exp)) {
                        System.err.println(String.format("无法获取到表达式,请检查表达式配置,表达式Key=%s,添加新的表达式后重启服务才能生效!", strs[0]));
                        throw new RuntimeException();
                    }
                    // 如果需要, 则准备参数
                    if (parmList != null) {
                        for (int i = 1; i < strs.length; i++) {
                            parmList.add(getSqlParam(strs[i]));
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("预处理自定义查找框表达式异常，Exp=" + exp);
            }
        }

        // 动态解析变量和逻辑运算
        return parseExp(exp);
    }

    private String parseExp(String exp) {
        return DynamicParse.buildSql(exp, (User) this.getSessionAttr(EovaConst.USER));
    }

}