/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.core.menu;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eova.common.Easy;
import com.eova.common.base.BaseCache;
import com.eova.common.base.BaseController;
import com.eova.common.utils.db.DbUtil;
import com.eova.common.utils.xx;
import com.eova.config.EovaConfig;
import com.eova.config.EovaConst;
import com.eova.core.button.ButtonFactory;
import com.eova.core.menu.config.ChartConfig;
import com.eova.core.menu.config.MenuConfig;
import com.eova.core.menu.config.TreeConfig;
import com.eova.model.*;
import com.eova.template.common.config.TemplateConfig;
import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.NestedTransactionHelpException;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.activerecord.tx.TxConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 菜单管理
 *
 * @author Jieven
 * @date 2014-9-11
 */
public class MenuController extends BaseController {

    public void toAdd() {
        Integer pid = getParaToInt("parent_id");
        // pid == 0 是根节点
        if (pid != 0) {
            // 判断父节点是否为目录
            Menu menu = Menu.dao.findById(pid);
            if (!menu.getStr("type").equals(Menu.TYPE_DIR)) {
                renderHtml("您选择的节点类型不是目录,无法添加子菜单,请选择其他目录节点进行添加");
                return;
            }
        }
        keepPara("parent_id");
        render("/eova/menu/add.html");
    }

    public void toUpdate() {
        int pkValue = getParaToInt(1);
        Menu menu = Menu.dao.findById(pkValue);

        setAttr("menu", menu);

        render("/eova/menu/add.html");
    }

    /**
     * 菜单基本功能管理
     */
    public void toMenuFun() {
        int id = getParaToInt(0);
        Menu menu = Menu.dao.findById(id);

        setAttr("menu", menu);

        HashMap<Integer, List<Button>> btnMap = new HashMap<Integer, List<Button>>();

        List<Button> btns = Button.dao.findNoQueryByMenuCode(menu.getStr("code"));
        for (Button b : btns) {
            int group = b.getInt("group_num");
            List<Button> list = btnMap.get(group);
            if (list == null) {
                list = new ArrayList<Button>();
                btnMap.put(group, list);
            }
            list.add(b);
        }

        setAttr("btnMap", btnMap);

        render("/eova/menu/menuFun.html");
    }

    // 一键导入
    @Before(Tx.class)
    @TxConfig(xx.DS_EOVA)
    public void addAll() {
        if (!getPara(0, "").equals("eova")) {
            renderJson(new Easy("请输入校验码，防止误操作！！！！！"));
            return;
        }

        List<MetaObject> objects = MetaObject.dao.find("select * from eova_object where id >= 1100");
        for (MetaObject o : objects) {

            String menuCode = o.getStr("code");
            System.out.println("create " + menuCode);
            Menu menu = new Menu();
            menu.set("parent_id", 3);
            menu.set("name", o.getStr("name"));
            menu.set("code", menuCode);
            menu.set("type", TemplateConfig.SINGLE_GRID);

            // 菜单配置
            MenuConfig config = new MenuConfig();
            config.setObjectCode(o.getStr("code"));
            menu.setConfig(config);
            menu.save();

//			createMenuButton(menuCode, TemplateConfig.SINGLE_GRID, config);
            // 创建菜单按钮
            new ButtonFactory(TemplateConfig.SINGLE_GRID).build(menuCode, config);

            // 还原成默认状态
            o.set("diy_card", null);
            o.update();
        }
        // 新增菜单使缓存失效
        BaseCache.delSer(EovaConst.ALL_MENU);

        renderJson(new Easy("Auto Create Menu:" + objects.size(), true));
    }

    // 导出选中菜单数据
    public void doExport() {
        String ids = getPara(0);

        StringBuilder sb = new StringBuilder();

        String sql = "select * from eova_menu where id in (" + ids + ")";
        List<Record> objects = Db.use(xx.DS_EOVA).find(sql);
        DbUtil.generateSql(objects, "eova_menu", "id", sb);
        sb.append("\n");
        for (Record r : objects) {
            List<Record> btns = Db.use(xx.DS_EOVA).find("select * from eova_button where menu_code = ?", r.getStr("code"));
            DbUtil.generateSql(btns, "eova_button", "id", sb);
            sb.append("\n");
        }

        renderText(sb.toString());
    }

    /**
     * 新增菜单
     */
    @Before(Tx.class)
    @TxConfig(xx.DS_EOVA)
    public void add() {

        String menuCode = getPara("code");
        String type = getPara("type");

        String sql = "select * from eova_menu where code = ?";
        Menu temp = Menu.dao.findFirst(sql, menuCode);
        if (temp != null) {
            renderJson(new Easy("菜单编码不能重复"));
            return;
        }
        String icon = getPara("iconField");
        if (!xx.isEmpty(icon) && icon.equalsIgnoreCase("icon")) {
            renderJson(new Easy("Tree图标字段:字段名不能为icon(系统关键字，你可以改为：iconskip)"));
            return;
        }
        icon = getPara("treeGridIconField");
        if (!xx.isEmpty(icon) && icon.equalsIgnoreCase("icon")) {
            renderJson(new Easy("Tree图标字段:字段名不能为icon(系统关键字，你可以改为：iconskip)"));
            return;
        }

        try {

            Menu menu = new Menu();
            menu.set("parent_id", getPara("parent_id"));
            menu.set("iconskip", getPara("icon", ""));
            menu.set("name", getPara("name"));
            menu.set("code", menuCode);
            menu.set("order_num", getPara("indexNum"));
            menu.set("type", type);
            // menu.set("biz_intercept", getPara("bizIntercept", ""));

            String url = getPara("url", "");// 自定义业务URL

            // 自定义office模版
            String path = getPara("path", "");
            menu.set("url", type.equals(TemplateConfig.DIY) ? url : path);

            // 菜单配置
            MenuConfig config = new MenuConfig();
            // 构建菜单配置项
            buildConfig(type, config);
            menu.setConfig(config);
            menu.save();

            // 目录没有默认按钮
            if (type.equals(Menu.TYPE_DIR)) {
                renderJson(new Easy());
                return;
            }

            // 创建菜单按钮
            new ButtonFactory(type).build(menuCode, config);

            // 新增菜单使缓存失效
            BaseCache.delSer(EovaConst.ALL_MENU);

            // EovaCloud.app();

            renderJson(new Easy());
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(new Easy("新增菜单失败,请仔细查看控制台日志！"));
            throw new NestedTransactionHelpException("新增菜单异常");
        }

    }

    /**
     * 配置菜单
     *
     * @param type   模版类型
     * @param config
     */
    private void buildConfig(String type, MenuConfig config) {
        if (type.equals(TemplateConfig.SINGLE_GRID)) {
            // 单表
            config.setObjectCode(getPara("objectCode"));
        } else if (type.equals(TemplateConfig.SINGLE_TREE)) {
            // 单表树
            config.setObjectCode(getPara("singleTreeObjectCode"));// TODO 此参数后续应去掉,tree配置应在一起.

            TreeConfig tc = new TreeConfig();
            tc.setObjectCode(config.getObjectCode());
            tc.setRootPid(getPara("rootPid"));
            tc.setIdField(getPara("idField", "id"));
            tc.setParentField(getPara("parentField"));
            tc.setTreeField(getPara("treeField"));
            tc.setIconField(getPara("iconField"));
            tc.setOrderField(getPara("orderField"));
            config.setTree(tc);
        } else if (type.equals(TemplateConfig.SINGLE_CHART)) {
            // 单表图
            config.setObjectCode(getPara("singleChartObjectCode"));

            String ys = getPara("singleChartY");
            List<String> ens = Arrays.asList(ys.split(","));

            // 根据字段英文名，获取字段中文名
            List<String> ycn = new ArrayList<>();
            List<MetaField> fields = MetaField.dao.queryFields(config.getObjectCode());
            for (String en : ens) {
                for (MetaField f : fields) {
                    if (f.getEn().equals(en)) {
                        ycn.add(f.getCn());
                        break;
                    }
                }
            }

            ChartConfig cc = new ChartConfig();
            cc.setType(getParaToInt("singleChartType"));
            cc.setX(getPara("singleChartX"));
            cc.setYunit(getPara("singleChartYunit"));
            cc.setY(ens);
            cc.setYcn(ycn);
            config.setChart(cc);

        } else if (type.equals(TemplateConfig.MASTER_SLAVE_GRID)) {
            // 主
            String masterObjectCode = getPara("masterObjectCode");
            String masterFieldCode = getPara("masterFieldCode");
            config.setObjectCode(masterObjectCode);
            config.setObjectField(masterFieldCode);

            // 子
            ArrayList<String> objects = new ArrayList<String>();
            ArrayList<String> fields = new ArrayList<String>();
            for (int i = 1; i <= 5; i++) {
                String slaveObjectCode = getPara("slaveObjectCode" + i);
                String slaveFieldCode = getPara("slaveFieldCode" + i);
                if (xx.isOneEmpty(slaveObjectCode, slaveFieldCode)) {
                    break;
                }
                objects.add(slaveObjectCode);
                fields.add(slaveFieldCode);
            }
            config.setObjects(objects);
            config.setFields(fields);
        } else if (type.equals(TemplateConfig.TREE_GRID)) {
            // 树&表
            TreeConfig tc = new TreeConfig();
            tc.setObjectCode(getPara("treeGridTreeObjectCode"));
            tc.setObjectField(getPara("treeGridTreeFieldCode"));

            tc.setIconField(getPara("treeGridIconField"));
            tc.setTreeField(getPara("treeGridTreeField"));
            tc.setParentField(getPara("treeGridParentField"));
            tc.setIdField(getPara("treeGridIdField", "id"));
            tc.setRootPid(getPara("treeGridRootPid"));
            config.setTree(tc);

            config.setObjectCode(getPara("treeGridObjectCode"));
            config.setObjectField(getPara("treeGridFieldCode"));
        } else if (type.equals(TemplateConfig.OFFICE)) {
            config.getParams().put("office_type", getPara("office_type"));
        }

    }

    /**
     * 菜单功能管理
     */
    @Before(Tx.class)
    @TxConfig(xx.DS_EOVA)
    public void menuFun() {
        String menuCode = getPara(0);

        List<Button> btns = Button.dao.findNoQueryByMenuCode(menuCode);
        // 动态获取按钮是否禁用
        for (Button btn : btns) {
            String ck = getPara("btn" + btn.getInt("id"));
            // 选中表示可被分配
            // 未选中表示不可分配
            btn.set("is_hide", xx.isEmpty(ck) || !ck.equals("on"));
            btn.update();
        }

        renderJson(new Easy());
    }

    /**
     * 是否能配置基础功能
     */
    public void isFun() {
        int id = getParaToInt(0);
        Menu m = Menu.dao.findById(id);
        String type = m.getStr("type");
        // 目录和自定义功能,不能配置基本功能
        if (type.equals(Menu.TYPE_DIR) || type.equals(Menu.TYPE_DIY)) {
            renderJson(Ret.fail());
            return;
        }
        renderJson(Ret.ok());
    }

    // eova_button 升级 V1.4/1.5 -> V1.6
    @Before(Tx.class)
    @TxConfig(xx.DS_EOVA)
    public void v16ButtonUpdate() {

        boolean isUpgrade = xx.getConfigBool("isUpgrade", false);
        if (!isUpgrade) {
            renderText("未开启升级模式，请启动配置 isUpgrade = true");
            return;
        }

        String sql = "select distinct(menu_code) from eova_button";
        List<String> codes = Db.use(xx.DS_EOVA).query(sql);

        // 修正异常的 查询按钮
        int num = Db.use(xx.DS_EOVA).update("update eova_button set is_base = 1 where ui = 'query'");
        System.err.println("修复异常的按钮数=" + num);
        // 删除所有查询按钮和基础功能
        num = Db.use(xx.DS_EOVA).update("delete from eova_button where is_base = 1 or ui = 'query'");
        System.err.println("删除按钮数=" + num);

        for (String code : codes) {
            Menu menu = Menu.dao.findByCode(code);
            if (menu == null || menu.getStr("type").equals(Menu.TYPE_DIR)) {
                continue;
            }
            new ButtonFactory(menu.getStr("type")).build(code, menu.getConfig());
        }

        // 删除无效权限数据
        num = Db.use(xx.DS_EOVA).update("delete from eova_role_btn");
        System.err.println("权限数据被清空");

        // 初始化超管权限
        sql = "select id from eova_button where menu_code = ?";
        List<Integer> ids = Db.use(xx.DS_EOVA).query(sql, "sys_auth_role");
        for (Integer id : ids) {
            RoleBtn rb = new RoleBtn();
            rb.set("bid", id);
            rb.set("rid", EovaConst.ADMIN_RID);
            rb.save();
        }

        initNewMenu();

        // 初始化EOVA按钮
        initEovaButton();


        renderText(" V1.4/1.5 -> V1.6 升级成功,请重新对所有角色重新进行权限分配！");
    }

    /**
     * 初始化新菜单(没有生成按钮的菜单)
     */
    @Before(Tx.class)
    @TxConfig(xx.DS_EOVA)
    public void initNewMenu() {

        String isUpgrade = EovaConfig.getProps().get("isUpgrade");
        if (xx.isEmpty(isUpgrade) || !isUpgrade.equals("true")) {
            renderText("未开启升级模式，请启动配置 eova.config isUpgrade = true");
            return;
        }

        String sql = "select code from eova_menu where type not in ('dir') and code not in (select DISTINCT(menu_code) from eova_button);";
        List<String> codes = Db.use(xx.DS_EOVA).query(sql);

        for (String code : codes) {
            System.out.println(code);
            Menu menu = Menu.dao.findByCode(code);
            new ButtonFactory(menu.getStr("type")).build(code, menu.getConfig());
        }

        System.err.println("自动修复未生成按钮的菜单成功");

        renderText("自动为没有按钮的菜单初始化成功！");
    }

    /**
     * 初始化指定菜单的基础按钮
     */
    @Before(Tx.class)
    @TxConfig(xx.DS_EOVA)
    public void initMenuButton() {
        JSONArray selectRows = getSelectRows();
        for (int i = 0; i < selectRows.size(); i++) {
            JSONObject o = selectRows.getJSONObject(i);
            String menuCode = o.getString("code");
            // 删除所有基础按钮
            Db.use(xx.DS_EOVA).update("delete from eova_button where is_base = 1 and menu_code = ?", menuCode);
            Menu menu = Menu.dao.findByCode(menuCode);
            new ButtonFactory(menu.getStr("type")).build(menuCode, menu.getConfig());

            System.err.println("初始化菜单按钮成功：" + menuCode);
        }

        renderJson(new Easy());
    }

    /**
     * 初始化EOVA按钮信息
     */
    @Before(Tx.class)
    @TxConfig(xx.DS_EOVA)
    public void initEovaButton() {

        String isUpgrade = EovaConfig.getProps().get("isUpgrade");
        if (xx.isEmpty(isUpgrade) || !isUpgrade.equals("true")) {
            renderText("未开启升级模式，请启动配置 eova.config isUpgrade = true");
            return;
        }

        {
            String sql = "UPDATE eova_button SET is_hide = 1 WHERE menu_code = ? and name = ?";
            Db.use(xx.DS_EOVA).update(sql, "eova_menu", "新增");
        }
        {
            String sql = "UPDATE eova_button SET is_hide = 1 WHERE menu_code = ? and name = ?";
            Db.use(xx.DS_EOVA).update(sql, "eova_menu", "查看");
        }
        {
            String sql = "UPDATE eova_button SET is_hide = 1 WHERE menu_code = ? and name = ?";
            Db.use(xx.DS_EOVA).update(sql, "eova_task", "导入");
        }
        {
            String sql = "UPDATE eova_button SET is_hide = 1 WHERE menu_code = ? and name = ?";
            Db.use(xx.DS_EOVA).update(sql, "eova_object", "新增");
        }
        {
            String sql = "UPDATE eova_button SET is_hide = 1 WHERE menu_code = ? and name = ?";
            Db.use(xx.DS_EOVA).update(sql, "eova_object", "查看");
        }
        {
            String sql = "UPDATE eova_button SET is_hide = 1 WHERE menu_code = ? and name = ?";
            Db.use(xx.DS_EOVA).update(sql, "eova_object", "新增");
        }
        {
            String sql = "UPDATE eova_button SET is_hide = 1 WHERE menu_code = ? and name = ?";
            Db.use(xx.DS_EOVA).update(sql, "sys_auth_users", "查看");
        }
        {
            String sql = "UPDATE eova_button SET is_hide = 1 WHERE menu_code = ? and name = ?";
            Db.use(xx.DS_EOVA).update(sql, "sys_auth_users", "用户详细信息新增");
        }
        {
            String sql = "UPDATE eova_button SET is_hide = 1 WHERE menu_code = ? and name = ?";
            Db.use(xx.DS_EOVA).update(sql, "sys_auth_users", "用户详细信息删除");
        }
        {
            String sql = "UPDATE eova_button SET is_hide = 1 WHERE menu_code = ? and name = ?";
            Db.use(xx.DS_EOVA).update(sql, "sys_auth_role", "查看");
        }
        {
            String sql = "UPDATE eova_button SET is_hide = 1 WHERE menu_code = ? and name = ?";
            Db.use(xx.DS_EOVA).update(sql, "sys_auth_role", "导入");
        }
        {
            String sql = "UPDATE eova_button SET is_hide = 1 WHERE menu_code = ? and ui <> 'query'";
            Db.use(xx.DS_EOVA).update(sql, "sys_log");
        }

        System.err.println("初始化EOVA按钮信息成功！");

        renderText("初始化EOVA按钮信息成功！");
    }

    /**
     * 初始化Oracle类型
     * 场景:Mysql迁移到Oracle
     */
    @Before(Tx.class)
    @TxConfig(xx.DS_EOVA)
    public void initOracleType() {

        String isUpgrade = EovaConfig.getProps().get("isUpgrade");
        if (xx.isEmpty(isUpgrade) || !isUpgrade.equals("true")) {
            renderText("未开启升级模式，请启动配置 eova.config isUpgrade = true");
            return;
        }

        {
            // VARCHAR2
            String sql = "UPDATE EOVA_FIELD SET DATA_TYPE_NAME = 'VARCHAR2' WHERE DATA_TYPE_NAME = 'VARCHAR' OR  DATA_TYPE_NAME = 'TEXT'";
            Db.use(xx.DS_EOVA).update(sql);
        }
        {
            // NUMBER
            String sql = "UPDATE EOVA_FIELD SET DATA_TYPE_NAME = 'NUMBER' WHERE DATA_TYPE_NAME LIKE '%INT%'";
            Db.use(xx.DS_EOVA).update(sql);
        }
        {
            // FLOAT
            String sql = "UPDATE EOVA_FIELD SET DATA_TYPE_NAME = 'NUMBER', DATA_DECIMAL = 1 WHERE DATA_TYPE_NAME LIKE '%FLOAT%'";
            Db.use(xx.DS_EOVA).update(sql);
        }
        {
            // DOUBLE
            String sql = "UPDATE EOVA_FIELD SET DATA_TYPE_NAME = 'NUMBER', DATA_DECIMAL = 8 WHERE DATA_TYPE_NAME LIKE '%DOUBLE%'";
            Db.use(xx.DS_EOVA).update(sql);
        }
        {
            // DECIMAL
            String sql = "UPDATE EOVA_FIELD SET DATA_TYPE_NAME = 'NUMBER', DATA_DECIMAL = 20 WHERE DATA_TYPE_NAME = 'DECIMAL'";
            Db.use(xx.DS_EOVA).update(sql);
        }
        {
            // CHAR
            String sql = "UPDATE EOVA_FIELD SET DATA_TYPE_NAME = 'CHAR', DATA_SIZE = 1 WHERE DATA_TYPE_NAME = 'BIT'";
            Db.use(xx.DS_EOVA).update(sql);
        }
        {
            // DATE
            String sql = "UPDATE EOVA_FIELD SET DATA_TYPE_NAME = 'DATE' WHERE DATA_TYPE_NAME LIKE '%DATE%' OR DATA_TYPE_NAME LIKE '%TIME%'";
            Db.use(xx.DS_EOVA).update(sql);
        }


        System.err.println("初始化Oracle类型成功！");

        renderText("初始化Oracle类型成功！");
    }

}