/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.engine;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.eova.common.utils.db.SqlUtil;
import com.eova.common.utils.xx;
import com.eova.config.EovaConfig;
import com.eova.model.MetaField;
import com.eova.model.MetaObject;
import com.jfinal.kit.LogKit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Eova表达式解析器
 *
 * @author Jieven
 */
public class EovaExp {

    private final HashMap<String, String> param = new HashMap<>();
    public String ds;
    public String sql;
    public String select;
    public String simpleSelect;
    public String from;
    public String table;
    public String where = "";
    public String order = "";
    public String pk;
    public String cn;
    protected SqlParse sp;

    public EovaExp() {
    }

    public EovaExp(String exp) {
        try {
            // 无参
            if (!exp.contains(";")) {
                this.sql = exp;
            }
            // 有参
            else {
                String[] strs = exp.split(";");
                this.sql = strs[0];
                // 除了SQL后续皆为参数键值对
                int i = 0;
                for (String s : strs) {
                    i++;
                    if (i == 1) {
                        continue;
                    }
                    s = s.trim();
                    String[] ss = s.split("=");
                    param.put(ss[0].trim(), ss[1].trim());
                }
            }

            this.ds = getPara(EovaExpParam.DS);

            // SQL转小写,常用查询关键字全小写,业务部分不动
            //			String[] keyword = { "SELECT", "FROM", "WHERE", "AND", "OR", "IN", "ORDER BY", "DESC", "NOT" };
            //			sql = sql.trim();
            //			for (String s : keyword) {
            //				sql = sql.replaceAll(s, s.toLowerCase());
            //			}
            // TODO 不方便获取数据源,暂时写Mysql,原始需求:常用查询关键字全小写,业务部分不动
            //			sql = sql.trim();
            //			sql = SqlUtil.notNewLine(SQLUtils.formatMySql(sql, new FormatOption(false)));

            // E001 SQL部分统一小写
            this.sql = sql.trim().toLowerCase();

            // 初始化SQL解析器
            sp = new SqlParse(EovaConfig.EOVA_DBTYPE, sql);

            // 解析Sql
            initParse();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        // 普通默认数据源
        print("select value ID,name CN from dicts where object = 'hotel' and field = 'state'");
        //		print("select id ID, pid PID, name CN, icon ICON from eova_menu;type=role");
        //		// 多字段排序
        //		print("select g.id ID,g.name 昵称 from game g where 1=1 and id = 3 order by id ,indexNum desc;ds=main");
        //		// 单字排序
        //		print("select id id,nickname cn from users where id > 1 order by id desc");
        //		// 子查询
        //		print("select id ID,name CN from xx where id in(select id from xxx)");
        //		// 多表联查带别名
        //		print("select a.id ID,a.name CN from a,b where a.id = b.aid and a.id > 1 order by a.id");
        //		// 多表联查带别名
        //		print("select user.id as myid,user.name,info.name,info.value,g.name,g.sc from user left join info on user.id = info.uid left join gift g on g.uid = user.id where user.id > 10");
    }

    private static void print(String exp) {
        EovaExp se = new EovaExp(exp);
        System.out.println(SqlUtil.notNewLine(se.sql));
        System.out.println();
        System.out.println("DS:" + se.ds);
        System.out.println("SS:" + se.simpleSelect);
        System.out.println("SELECT:" + se.select);
        System.out.println("FROM:" + se.from);
        System.out.println("WHERE:" + se.where);
        System.out.println("ORDER:" + se.order);
        System.out.println("PK:" + se.pk);
        System.out.println("CN:" + se.cn);
        System.out.println("param:" + se.param);
        System.out.println();
        // SQLUtils.formatMySql(se.sql, new FormatOption(false));
    }

    /**
     * 手工组装字段元数据
     *
     * @param index   排序
     * @param en      英文名
     * @param cn      中文名
     * @param isQuery 是否可查询
     * @return
     */
    public static MetaField buildItem(int index, String en, String cn, boolean isQuery) {

        if (xx.isEmpty(cn))
            cn = en;

        en = en.toLowerCase();
        // 获取元模版字段
        MetaField ei = MetaField.dao.getTemplate();
        ei.put("order_num", index);
        ei.put("en", en);
        ei.put("cn", xx.isEmpty(cn) ? en : cn);
        ei.put("type", "文本框");
        ei.put("is_query", isQuery);
        // 宽度智能计算,表格列头越长,宽度越大
        ei.put("width", 80 + cn.length() * 20);// 自适应 "undefined"
        // 第一列如果没有别名隐藏:如不想显示ID或UUID
        if (index == 1 && en.equals(cn)) {
            ei.put("is_show", false);
        }
        return ei;
    }

    public void initParse() {
        List<SQLSelectItem> items = sp.getSelectItem();
        try {
            // 第1列默认为主键
            this.pk = SqlParse.getExprName(items.get(0).getExpr());
            // 获取自定义CN字段名
            String cname = getPara(EovaExpParam.CNAME);
            if (xx.isEmpty(cname)) {
                // 否则取第2列默认为CN键
                cname = SqlParse.getExprName(items.get(1).getExpr());
            }
            this.cn = cname;
        } catch (Exception e) {
            LogKit.debug("EovaExp initParse Select Item Error:" + sp.getSelectItem());
        }
        buildSelect();
        buildSimpleSelect();
        buildFrom();
        buildTable();
        buildWhere();
        buildOrder();
    }

    private void buildSelect() {
        this.select = "select " + sp.getSelectItem().toString().replaceAll("\\[|\\]", "");
    }

    private void buildSimpleSelect() {
        StringBuilder sb = new StringBuilder("select ");
        for (SQLSelectItem item : sp.getSelectItem()) {
            sb.append(item.getExpr()).append(", ");
        }
        sb.delete(sb.length() - 2, sb.length() - 1);
        this.simpleSelect = sb.toString().trim();
    }

    private void buildFrom() {
        SQLTableSource ts = sp.query.getFrom();
        String alias = ts.getAlias();

        String s = " from " + ts.toString();
        if (alias != null) {
            s += ' ' + alias;
        }

        this.from = SqlUtil.notNewLine(s);
    }

    private void buildTable() {
        this.table = this.from.toLowerCase().replace(" from ", "");
    }

    private void buildWhere() {
        SQLExpr exp = sp.query.getWhere();
        if (exp == null) {
            this.where = "";
            return;
        }
        String s = " where " + SQLUtils.toSQLString(exp);
        this.where = SqlUtil.notNewLine(s);
    }

    private void buildOrder() {
        StringBuilder sb = new StringBuilder(" order by ");

        List<SQLSelectOrderByItem> items = sp.getOrderItem();
        if (items == null) {
            return;
        }
        for (SQLSelectOrderByItem x : items) {
            // SQLIdentifierExpr exp = (SQLIdentifierExpr) x.getExpr();
            // sb.append(exp.getName());
            // if (x.getType() != null) {
            // sb.append(' ' + x.getType().name());
            // }
            sb.append(SQLUtils.toSQLString(x));
            sb.append(',');
        }
        sb.delete(sb.length() - 1, sb.length());

        this.order = sb.toString();
    }

    /**
     * 构建元对象
     *
     * @param exp 表达式
     * @return
     */
    public MetaObject getObject() {
        // 获取元对象模版
        MetaObject eo = MetaObject.dao.getTemplate();
        eo.put("data_source", ds);
        eo.put("name", "");
        // 获取第一的值作为主键
        eo.put("pk_name", pk);
        // 获取第二列的值作为CN
        eo.put("cn", cn);

        return eo;
    }

    /**
     * 构建元字段属性
     *
     * @param exp 表达式
     * @return
     */
    public List<MetaField> getFields() {
        try {
            List<MetaField> fields = new ArrayList<MetaField>();
            int index = 0;
            List<SQLSelectItem> items = sp.getSelectItem();
            for (SQLSelectItem item : items) {
                index++;

                SQLIdentifierExpr expr = (SQLIdentifierExpr) item.getExpr();

                // 字段名->字段名
                String en = expr.getName();
                // 字段别名->字段列名
                String cn = item.getAlias();

                // 首列之后的默认都可以查询
                boolean isQuery = true;
                if (index == 1) {
                    isQuery = false;
                }
                fields.add(buildItem(index, en, cn, isQuery));
            }
            return fields;
        } catch (Exception e) {
            throw new RuntimeException("Eova表达式构建虚拟元字段异常:" + e.getMessage());
        }
    }

    /**
     * 获取自定义参数
     *
     * @param key          参数名
     * @param defaultValue 默认值
     * @return
     */
    public String getPara(String key, String defaultValue) {
        String val = getPara(key);
        if (xx.isEmpty(val)) {
            return defaultValue;
        }
        return val;
    }

    public String getPara(String key) {
        return param.get(key);
    }

    public String getPara(EovaExpParam eep) {
        return getPara(eep.getVal(), eep.getDef());
    }

    /**
     * 表达式动态变更后的SQL
     */
    public String toString() {
        return this.select + this.from + this.where + this.order;
    }
}