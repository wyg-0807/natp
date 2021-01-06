/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.core.meta;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eova.cloud.EovaCloud;
import com.eova.common.Easy;
import com.eova.common.base.BaseController;
import com.eova.common.utils.db.DbUtil;
import com.eova.common.utils.db.DsUtil;
import com.eova.common.utils.xx;
import com.eova.config.EovaConfig;
import com.eova.config.EovaConst;
import com.eova.engine.EovaExp;
import com.eova.model.MetaField;
import com.eova.model.MetaObject;
import com.eova.model.User;
import com.jfinal.aop.Before;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.activerecord.tx.TxConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 元数据相关获取
 *
 * @author Jieven
 */
public class MetaController extends BaseController {

    // 获取元对象
    public void object() {
        String code = getPara(0);
        MetaObject mo = MetaObject.dao.getByCode(code);
        // 安全保护，去掉界面不需要的字段
        mo.remove("filter");
        mo.remove("table_name");
        mo.remove("view_name");
        mo.remove("view_sql");
        renderJson(JsonKit.toJson(mo));
    }

    // 获取元字段集
    public void fields() {
        String code = getPara(0);
        User user = getSessionAttr(EovaConst.USER);
        List<MetaField> fields = MetaField.dao.queryFields(code, user);
        // 安全保护，去掉界面不需要的字段
        for (MetaField f : fields) {
            f.remove("exp");
            f.remove("table_name");
            f.remove("data_type");
            f.remove("data_type_name");
            f.remove("data_size");
            f.remove("data_decimal");
        }
        renderJson(JsonKit.toJson(fields));
    }

    // 编辑元数据
    public void edit() {
        String objectCode = getPara(0);
        setAttr("objectCode", objectCode);
        render("/eova/meta/edit.html");
    }

    // 导入页面
    public void imports() {
        setAttr("dataSources", EovaConfig.getDataSources());
        render("/eova/meta/import.html");
    }

    // 查找表结构表头
    public void find() {

        String ds = getPara(0);
        String type = getPara(1);
        // 根据表达式手工构建Eova_Object
        MetaObject eo = MetaObject.dao.getTemplate();
        eo.put("data_source", ds);
        // 第1列名
        eo.put("pk_name", "table_name");
        // 第2列名
        eo.put("cn", "table_name");

        // 根据表达式手工构建Eova_Item
        List<MetaField> eis = new ArrayList<MetaField>();
        eis.add(EovaExp.buildItem(1, "table_name", "编码", false));
        eis.add(EovaExp.buildItem(2, "table_name", "表名", true));

        setAttr("objectJson", JsonKit.toJson(eo));
        setAttr("fieldsJson", JsonKit.toJson(eis));
        setAttr("itemList", eis);
        setAttr("pk", "pk_name");

        setAttr("action", "/meta/findJson/" + ds + '-' + type);
        setAttr("isPaging", false);

        render("/eova/widget/find/find.html");
    }

    // 查找表结构数据
    public void findJson() {

        // 获取数据库
        String ds = getPara(0);
        String type = getPara(1);

        // 表名过滤
        String tableNamePattern = getPara("query_table_name");
        if (!xx.isEmpty(tableNamePattern)) {
            tableNamePattern = "%" + tableNamePattern + "%";
        }

        List<String> tables = DsUtil.getTableNamesByConfigName(ds, type, tableNamePattern);
        JSONArray tableArray = new JSONArray();
        for (String tableName : tables) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("table_name", tableName);
            tableArray.add(jsonObject);
        }

        // 构建JSON数据
        String ui = xx.getConfig("ui", "easyui");
        if (ui.equals("easyui")) {
            ui = "{\"total\":%s,\"rows\": %s}";
        } else if (ui.equals("layui")) {
            ui = "{\"code\": 0, \"msg\": \"\", \"count\":\"%s\",\"data\": %s}";
        }

        renderJson(String.format(ui, tableArray.size(), JsonKit.toJson(tableArray)));
    }

    // 一键导入
    @Before(Tx.class)
    @TxConfig(xx.DS_EOVA)
    public void importAll() {

        if (!getPara(0, "").equals("eova")) {
            renderJson(new Easy("请输入校验码，防止误操作！！！！！"));
            return;
        }

        boolean isUpgrade = xx.getConfigBool("isUpgrade", false);
        if (!isUpgrade) {
            renderText("未开启升级模式，请启动配置 isUpgrade = true");
            return;
        }

        String ds = xx.DS_MAIN;
        String type = DsUtil.TABLE;

        // DB名
        String db = DsUtil.getDbNameByConfigName(ds);

        // 获取所有表名
        List<String> tables = DsUtil.getTableNamesByConfigName(ds, type, null);

        for (String table : tables) {

            if (table.startsWith("eova_") || table.equals("dicts") || table.equals("user_info")) {
                continue;
            }
            // if (!table.equals("dicts")) {
            // continue;
            // }
            System.out.println(table);

            String name = table;
            String code = db + "_" + table;

            if (xx.isMysql()) {
                // 自动获取表名注释
                String s = Db.queryStr("select TABLE_COMMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?", db, table);
                if (!xx.isEmpty(s)) {
                    name = s;
                }
            }

            // 导入元数据
            String msg = importMeta(ds, type, table, name, code, "id");
            if (!xx.isEmpty(msg)) {
                LogKit.error(msg);
            }
        }

        renderJson(new Easy());
    }

    // 导入元数据
    @Before(Tx.class)
    @TxConfig(xx.DS_EOVA)
    public void doImports() {

        String ds = getPara("ds");
        String type = getPara("type");
        String table = getPara("table");
        String name = getPara("name");
        String code = getPara("code");
        String pk = getPara("pk");

        if (xx.isOneEmpty(ds, type, table, name, code)) {
            renderJson(new Easy("参数都必须填写！"));
            return;
        }

        MetaObject o = MetaObject.dao.getByCode(code);
        if (o != null) {
            renderJson(new Easy(String.format("对象编码[%s]已经被其它对象使用了，请修改对象编码！", code)));
            return;
        }

        // 导入元数据
        String msg = importMeta(ds, type, table, name, code, pk);
        if (!xx.isEmpty(msg)) {
            renderJson(new Easy(msg));
            return;
        }

        renderJson(new Easy());
    }

    // 导出选中元数据
    public void doExport() {
        String ids = getPara(0);

        StringBuilder sb = new StringBuilder();

        String sql1 = "select * from eova_object where id in (" + ids + ")";
        List<Record> objects = Db.use(xx.DS_EOVA).find(sql1);
        DbUtil.generateSql(objects, "eova_object", "id", sb);

        sb.append("\n\n");

        String sql2 = "select * from eova_field where object_code in (select code from eova_object where id in (" + ids + "))";
        List<Record> fields = Db.use(xx.DS_EOVA).find(sql2);
        DbUtil.generateSql(fields, "eova_field", "id", sb);

        renderText(sb.toString());
    }

    // 覆盖同步元字段(主键需要更新元对象)
    @Before(Tx.class)
    @TxConfig(xx.DS_EOVA)
    public void override() {
        String str = getPara(0);
        String[] ids = str.split(",");
        for (String id : ids) {
            MetaObject o = MetaObject.dao.findById(id);
            // 删除元数据
            MetaObject.dao.deleteById(id);
            MetaField.dao.deleteByObjectCode(o.getCode());
            // 导入元数据
            importMetaObject(o.getDs(), o.getType(), o.getView(), o.getName(), o.getCode(), o.getPk());
            importMetaField(o.getDs(), o.getView(), o.getCode(), o.isView());
        }

        renderJson(new Easy());
    }

    // 增量同步元字段
    @Before(Tx.class)
    @TxConfig(xx.DS_EOVA)
    public void syncnew() {

        // 根据元对象编码同步
        String code = getPara("objectCode");
        if (!xx.isEmpty(code)) {
            MetaObject mo = MetaObject.dao.getByCode(code);
            buildSyncNew(mo);
            renderJson(new Easy());
            return;
        }

        // 批量同步
        String str = getPara(0);
        String[] ids = str.split(",");
        for (String id : ids) {
            MetaObject mo = MetaObject.dao.findById(id);

            buildSyncNew(mo);
        }

        renderJson(new Easy());
    }

    private void buildSyncNew(MetaObject mo) {
        List<MetaField> fields = MetaField.dao.queryByObjectCode(mo.getCode());

        String ds = mo.getDs();
        String table = mo.getView();

        JSONArray list = DsUtil.getColumnInfoByConfigName(ds, table, mo.isView());

        // 如果当前元字段中不存在就新增
        for (int i = 0; i < list.size(); i++) {
            JSONObject o = list.getJSONObject(i);
            String name = o.getString("COLUMN_NAME");

            // 是否新字段
            boolean isNew = true;
            for (MetaField field : fields) {
                if (name.equalsIgnoreCase(field.getEn())) {
                    isNew = false;
                }
            }

            // 新字段进行导入
            if (isNew) {
                ColumnMeta col = new ColumnMeta(ds, table, o);
                MetaField mi = new MetaField(mo.getCode(), col);
                mi.save();

                autoBindDict(table, mo.getCode(), o.getString("REMARKS"), mi.getEn());
            }
        }
    }

    // 复制元数据
    @Before(Tx.class)
    @TxConfig(xx.DS_EOVA)
    public void copy() {
        String id = getPara(0);
        String code = getPara(1);

        MetaObject object = MetaObject.dao.findById(id);
        List<MetaField> fields = MetaField.dao.queryByObjectCode(object.getCode());

        // 使用新的对象编码,生成一份元数据
        for (MetaField f : fields) {
            f.remove("id");
            f.set("object_code", code);
            f.save();
        }
        object.remove("id");
        object.set("code", code);
        object.save();

        renderJson(new Easy());
    }

    /**
     * 导入元数据
     *
     * @param ds    数据源
     * @param type  表还是视图
     * @param table 表名
     * @param name  对象名
     * @param code  对象编码
     * @param pk    主键名
     */
    public String importMeta(String ds, String type, String table, String name, String code, String pk) {

        boolean isView = type.equalsIgnoreCase(DsUtil.VIEW);
        // table自动获取主键
        if (!isView) {
            pk = DsUtil.getPkName(ds, table);
            if (xx.isEmpty(pk)) {
                return "表的主键不能为空，请为当前表设置主键！";
            }
        }

        // G001 表名+主键 小写
        table = table.toLowerCase();
        pk = pk.toLowerCase();

        // 导入元字段
        importMetaField(ds, table, code, isView);
        // 导入元对象
        importMetaObject(ds, type, table, name, code, pk);

        // 云端人工智能预处理元数据
        EovaCloud.buildMeta(code);

        return null;
    }

    /**
     * 导入元字段
     *
     * @param ds     数据源
     * @param table  表名
     * @param code   对象编码
     * @param isView 是否视图
     */
    private void importMetaField(String ds, String table, String code, boolean isView) {
        JSONArray list = DsUtil.getColumnInfoByConfigName(ds, table, isView);

        for (int i = 0; i < list.size(); i++) {
            JSONObject o = list.getJSONObject(i);

            ColumnMeta col = new ColumnMeta(ds, table, o);
            MetaField mi = new MetaField(code, col);
            mi.save();

            autoBindDict(table, code, o.getString("REMARKS"), mi.getEn());
        }
    }

    /**
     * 自动根据字典 将对应的字段 设置成 下拉框 并生成表达式
     *
     * @param tableName
     * @param objectCode
     * @param remarks
     * @param fieldName
     */
    private void autoBindDict(String tableName, String objectCode, String remarks, String fieldName) {
        if (xx.isEmpty(remarks)) {
            return;
        }
        if ((remarks.contains(":") || remarks.contains("：")) && remarks.contains("=")) {
            String dictTable = EovaConfig.getProps().get("main_dict_table");
            String exp = String.format("select value ID,name CN from %s where object = '%s' and field = '%s'", dictTable, tableName, fieldName);
            String sql = "update eova_field set type = '下拉框', exp = ? where object_code = ? and en = ?";
            Db.use(xx.DS_EOVA).update(sql, exp, objectCode, fieldName);
            LogKit.info("自动绑定字典成功");
        }
    }

    /**
     * 导入元对象
     *
     * @param ds
     * @param type
     * @param table
     * @param name
     * @param code
     * @param pkName
     */
    private void importMetaObject(String ds, String type, String table, String name, String code, String pkName) {

        MetaObject mo = new MetaObject();
        // 编码
        mo.set("code", code);
        // 名称
        mo.set("name", name);
        // 主键
        mo.set("pk_name", pkName);
        // 数据源
        mo.set("data_source", ds);
        // 表或视图
        if (type.equalsIgnoreCase(DsUtil.TABLE)) {
            mo.set("table_name", table);
        } else {
            mo.set("view_name", table);
        }

        mo.save();
    }

    // 刷新元字段数据类型
    @Before(Tx.class)
    @TxConfig(xx.DS_EOVA)
    public void syncField() {

        List<MetaObject> os = MetaObject.dao.find("select * from eova_object");
        for (MetaObject o : os) {
            List<MetaField> fs = MetaField.dao.queryByObjectCode(o.getCode());

            String ds = o.getDs();
            String table = o.getView();

            JSONArray list = DsUtil.getColumnInfoByConfigName(ds, table, o.isView());
            for (int i = 0; i < list.size(); i++) {
                JSONObject json = list.getJSONObject(i);
                String name = json.getString("COLUMN_NAME");
                for (MetaField f : fs) {
                    // 更新数据类型
                    if (f.getEn().equalsIgnoreCase(name)) {
                        ColumnMeta col = new ColumnMeta(ds, table, json);
                        f.set("data_type", col.dataType);
                        f.set("data_type_name", col.dataTypeName);
                        f.set("data_size", col.dataSize);
                        f.set("data_decimal", col.dataDecimal);
                        // f.set("defaulter", col.defaultValue);
                        f.set("is_auto", col.isAuto);
                        // 自动纠正易错时间配置
                        if (col.dataTypeName.contains("TIMESTAMP") && col.dataTypeName.contains("DATETIME")) {
                            f.set("type", "时间框");
                        } else if (col.dataTypeName.contains("DATE")) {
                            f.set("type", "日期框");
                        }
                        f.update();
                    }
                }
            }
            LogKit.info("元数据刷新成功：" + o.getCode());
        }

        renderJson(new Easy());
    }

    // 拖拽改变序号
    @Before(Tx.class)
    @TxConfig(xx.DS_EOVA)
    public void swap() {
        int sid = getParaToInt(0);
        int snum = getParaToInt(1);
        int tid = getParaToInt(2);
        int tnum = getParaToInt(3);

        if (snum - tnum == Math.abs(1)) {
            // 相邻的直接交换位置
            MetaField.dao.swapOrderNum(sid, tid, snum, tnum);
        } else {
            // 不相邻的,直接放到目标的索引+1
            MetaField.dao.updateOrderNum(sid, tnum + 1);
        }

        renderJson(new Easy());
    }

    // 添加虚拟字段
    public void addVirtualField() {
        String en = getInputValue();
        String code = getPara("objectCode");// 场景1:先取快速编辑的参数
        if (xx.isEmpty(code)) {
            // 场景2:然后取元数据管理按钮参数
            code = getSelectValue("code");
        }

        MetaField template = MetaField.dao.getTemplate();
        template.set("object_code", code);
        template.set("table_name", EovaConst.VIRTUAL);// 通过这个来标识虚拟字段
        template.set("en", "v_" + en);// 虚拟字段统一前缀,方便识别
        template.set("cn", en);
        template.set("order_num", 999);
        template.set("formatter", "function(value,row,index,field){return value}");
        template.set("is_order", 0);// 禁止排序
        template.set("is_edit", 0);// 禁止编辑
        template.save();

        renderJson(Easy.sucess("添加虚拟字段成功"));
    }

}