/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.widget;

import com.eova.aop.AopContext;
import com.eova.aop.MetaObjectIntercept;
import com.eova.aop.eova.EovaContext;
import com.eova.common.utils.db.DbUtil;
import com.eova.common.utils.db.SqlUtil;
import com.eova.common.utils.xx;
import com.eova.config.EovaConfig;
import com.eova.config.EovaConst;
import com.eova.config.PageConst;
import com.eova.core.menu.config.TreeConfig;
import com.eova.core.object.config.MetaObjectConfig;
import com.eova.core.object.config.TableConfig;
import com.eova.engine.DynamicParse;
import com.eova.engine.EovaExp;
import com.eova.engine.EovaExpParam;
import com.eova.i18n.I18NBuilder;
import com.eova.model.Menu;
import com.eova.model.MetaField;
import com.eova.model.MetaObject;
import com.eova.model.User;
import com.eova.template.common.config.TemplateConfig;
import com.eova.template.common.util.TemplateUtil;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.*;

/**
 * 组件公共业务
 *
 * @author Jieven
 */
public class WidgetManager {

    /**
     * 构建查询列
     * 1.只查元数据列
     * 2.不查未授权列
     *
     * @param object 元数据
     * @param rid    当前角色ID
     * @return
     * @throws Exception
     */
    public static String buildSelect(MetaObject object, int rid) throws Exception {

        // 角色字段授权
        Map<String, Set<String>> authFields = EovaConfig.getAuthFields();

        List<String> cols = new ArrayList<>();
        for (MetaField f : object.getFields()) {
            if (f.isVirtual()) {
                continue;
            }
            String en = f.getEn();
            if (!xx.isEmpty(authFields)) {
                Set<String> auth = authFields.get(object.getCode() + "." + en);
                if (!xx.isEmpty(auth) && !auth.contains(rid + "")) {
                    // 未授权字段不能被查询
                    continue;
                }
            }
            // 字段中可能存在敏感关键字, 兼容但建议不要使用关键字,从源头上解决问题.
            cols.add(DbUtil.format(en));
        }
        return xx.join(cols);
    }
    /**
     * 构建表达式SQL(不包括select部分)
     *
     * @param ctrl
     * @param exp   Eova表达式
     * @param paras SQL动态参数
     * @return
     * @throws Exception
     */
    public static String buildExpSQL(Controller ctrl, EovaExp exp, List<Object> paras) throws Exception {

        String sql = exp.from;
        String where = exp.where;

        // + 全局拦截条件
        if (EovaConfig.getEovaIntercept() != null) {
            EovaContext ec = new EovaContext(ctrl);
            ec.exp = exp;
            String filter = EovaConfig.getEovaIntercept().filterExp(ec);
            where = SqlUtil.appendWhereCondition(where, filter);
        }

        // + 查询条件
        where = SqlUtil.appendWhereCondition(where, buildQueryCondition(ctrl, exp.getFields(), paras));
//		sql += where;

        // + 排序条件
        where += WidgetManager.getSort(ctrl, exp.order);

        // 美化SQL
        return SqlUtil.formatSql(sql + " " + where);
    }

    /**
     * 构建查询SQL(不包括select部分)
     *
     * @param ctrl
     * @param menu      当前菜单
     * @param object    当前元对象
     * @param intercept 元对象业务拦截器
     * @param parmList  动态SQL参数集
     * @param isQuery   是否有Form查询
     * @param select    查询项
     * @return
     * @throws Exception
     */
    public static String buildQuerySQL(Controller ctrl, Menu menu, MetaObject object, MetaObjectIntercept intercept, List<Object> parmList, boolean isQuery, String select) throws Exception {
        String sql = "";
        String where = "";

        Object user = ctrl.getSessionAttr(EovaConst.USER);

        // + 全局过滤条件
        if (EovaConfig.getEovaIntercept() != null) {
            EovaContext ec = new EovaContext(ctrl);
            ec.menu = menu;
            ec.object = object;
            where += EovaConfig.getEovaIntercept().filterQuery(ec);
        }

        // + 元对象过滤条件
        where = SqlUtil.appendWhereCondition(where, DynamicParse.buildSql(object.getStr("filter"), user));
        // + 菜单过滤条件
        if (menu != null) {
            where = SqlUtil.appendWhereCondition(where, DynamicParse.buildSql(menu.getStr("filter"), user));
        }

        // + 查询条件
        if (isQuery)
            where = SqlUtil.appendWhereCondition(where, buildQueryCondition(ctrl, object.getFields(), parmList));

        // + 排序条件
        String sort = WidgetManager.getSort(ctrl, object.getStr("default_order"));

        // 分页查询Grid数据
        String view = object.getView();
        sql = "from " + view;

        // 查询前置任务
        if (intercept != null) {
            AopContext ac = new AopContext(ctrl);
            ac.object = object;
            intercept.queryBefore(ac);

            // 自定义查询SQL
            if (!xx.isEmpty(ac.sql)) {
                select = "select *";// 自定义sql场景必须查全部
                sql = "from " + String.format("(%s) T", ac.sql);
                where = "";
                // sort = ""; 不能清空, 自定义sql 决定内容,排序需要追加在最外层
                parmList.clear();
            } else {
                // 追加条件
                if (!xx.isEmpty(ac.condition)) {
                    where += ac.condition;
                    if (!xx.isEmpty(ac.params))
                        parmList.addAll(ac.params);
                }
                // 覆盖条件
                if (!xx.isEmpty(ac.where)) {
                    where = ac.where;
                    parmList.clear();
                    if (!xx.isEmpty(ac.params))
                        parmList.addAll(ac.params);
                }
                // 覆盖排序
                if (!xx.isEmpty(ac.sort)) {
                    sort = ac.sort;
                }
            }
        }

        sql += String.format(" %s %s ", SqlUtil.buildWhere(where), sort);

        // 美化SQL
        return SqlUtil.formatSql(sql);
    }

    /**
     * 动态构建查询条件
     *
     * @param c
     * @param fields 元字段
     * @param params SQL动态参数
     * @return
     */
    private static String buildQueryCondition(Controller c, List<MetaField> fields, List<Object> params) {
        // 初始过滤条件
        StringBuilder sb = new StringBuilder();

        for (MetaField ei : fields) {
            // 跳过虚拟字段
            if (ei.isVirtual()) {
                continue;
            }

            String key = ei.getEn();
            // 给查询表单添加前缀，防止和系统级别字段重名
            String value = c.getPara(PageConst.QUERY + key, "").trim();
            String start = c.getPara(PageConst.START + key, "").trim();
            String end = c.getPara(PageConst.END + key, "").trim();

            // 当前字段 既无文本值 也无范围值，说明没填，直接跳过
            if ((xx.isEmpty(value) || value.equals("-1")) && xx.isAllEmpty(start, end)) {
                continue;
            }
            // 范围值只填一个，默认两个值相同
            if (xx.isEmpty(start))
                start = end;
            if (xx.isEmpty(end))
                end = start;

            // 布尔框需要转换值
            value = TemplateUtil.buildValue(ei, value).toString();

            // 多值条件
            if (ei.getBoolean("is_multiple")) {
                sb.append(" and (");
                for (String val : value.split(",")) {
                    if (!sb.toString().endsWith(" (")) {
                        sb.append(" or ");
                    }
                    if (xx.isMysql()) {
                        sb.append(String.format("FIND_IN_SET(?, %s)", key));
                        params.add(val);
                    } else if (xx.isOracle()) {
                        // 意思 在 ,1,2,21,22,30, 查找 ,2,
                        sb.append(String.format("instr(','||%s||',' ,  ',%s,') > 0", key, val));
                    } else {
                        sb.append(key + " like ").append("?");
                        params.add('%' + val + '%');
                    }
                }
                sb.append(")");
            }
            // 单值条件
            else {
                String type = ei.getStr("type");
                // 数字类型都是精确查询
                if (ei.getDataTypeName().toLowerCase().contains("int") && !xx.isEmpty(value)) {
                    sb.append(" and " + key + " = ?");
                    params.add(value);
                }
                // 文本类都是模糊条件
                else if (type.equals(MetaField.TYPE_TEXT) || type.equals(MetaField.TYPE_TEXTS) || type.equals(MetaField.TYPE_EDIT)) {
                    sb.append(" and " + key + " like ?");
                    params.add("%" + value + "%");
                }
                // 数值类的都是精准查询
                else if (type.equals(MetaField.TYPE_NUM)) {
                    String s = c.getPara(PageConst.COND + key, "").trim();

                    if (s.equals("=")) {
                        sb.append(" and ? = " + key);
                        params.add(start);
                    } else if (s.equals("<")) {
                        sb.append(" and ? > " + key);
                        params.add(start);
                    } else if (s.equals(">")) {
                        sb.append(" and ? < " + key);
                        params.add(start);
                    } else {
                        // 只选一个值无效
                        if (xx.isOneEmpty(start, end)) {
                            continue;
                        }

                        if (s.equals("∩")) {
                            sb.append(" and ? < " + key + " and " + key + " < ?");
                            params.add(start);
                            params.add(end);
                        } else if (s.equals("U")) {
                            sb.append(" and ? > " + key + " or " + key + " < ?");
                            params.add(start);
                            params.add(end);
                        }
                    }
                } else if (type.equals(MetaField.TYPE_TIME)) {
                    if (xx.isOracle()) {
                        sb.append(String.format(" and to_date(?,'yyyy-mm-dd hh24:mi:ss') <= %s and %s <= to_date(?,'yyyy-mm-dd hh24:mi:ss')", key, key));
                    } else {
                        sb.append(String.format(" and ? <= %s and %s <= ?", key, key));
                    }

                    params.add(start);
                    params.add(end);
                } else if (type.equals(MetaField.TYPE_DATE)) {
                    if (xx.isOracle()) {
                        sb.append(String.format(" and to_date(?,'yyyy-mm-dd') <= %s and %s < to_date(?,'yyyy-mm-dd') + 1", key, key));
                    } else {
                        sb.append(String.format(" and ? <= date(%s) and date(%s) <= ?", key, key));
                    }

                    params.add(start);
                    params.add(end);
                } else {
                    sb.append(" and " + key + " = ?");
                    params.add(value);
                }
            }


            // 保持条件值回显
            ei.put("value", value);
        }

        return sb.toString();
    }

    /**
     * 添加where条件
     *
     * @param se
     * @param condition
     * @return
     */
    public static String addWhere(EovaExp se, String condition) {

        String select = se.simpleSelect;
        String from = se.from;
        String where = se.where;

        if (xx.isEmpty(where)) {
            where = " where " + condition;
        } else {
            where += " and " + condition;
        }

        return select + from + where;
    }

    /**
     * 获取排序
     *
     * @param c
     * @param order 指定排序
     * @return
     */
    public static String getSort(Controller c, String order) {

        // 动态解析变量和逻辑运算 TODO 在外面都会解析表达式
        // order = DynamicParse.buildSql(order, c.getSessionAttr(EovaConst.USER));

        String sql = "";

        // 指定默认排序方式
        if (!xx.isEmpty(order)) {
            if (!order.toLowerCase().contains("order by"))
                sql += " order by ";
            sql += order;
        }

        // 当前Request的排序方式
        String orderField = c.getPara(PageConst.SORT, "");// 获取排序字段
        String orderType = c.getPara(PageConst.ORDER, "");// 获取排序方式
        if (!xx.isEmpty(orderField)) {
            sql = " order by " + orderField + ' ' + orderType;
        }

        return sql;
    }

    public static String getSort(Controller c) {
        return getSort(c, null);
    }

    public static void convertValueByExp(Controller c, List<MetaField> eis, List<Record> reList, String... excludeFields) {
        // 根据表达式翻译显示CN(获取当前字段所有的 查询结果值，拼接成 字符串 用于 结合表达式进行 in()查询获得cn 然后替换之)
        F1:
        for (MetaField ei : eis) {
            // 获取存在表达式的列名
            String en = ei.getEn();
            // 排除不需要翻译的字段
            if (!xx.isEmpty(excludeFields)) {
                for (String field : excludeFields) {
                    if (field.equals(en)) {
                        continue F1;
                    }
                }
            }
            // 只翻译需要显示的字段
            if (!ei.getBoolean("is_show")) {
                continue;
            }
            // 获取控件表达式
            String exp = ei.getStr("exp");
            if (xx.isEmpty(exp)) {
                continue;
            }
            // System.out.println(en + " EovaExp:" + exp);
            // in 条件值
            Set<String> ids = new HashSet<String>();
            if (!xx.isEmpty(reList)) {
                for (Record re : reList) {
                    String value = re.get(en, "").toString();
                    if (value.contains(",")) {
                        // 多值
                        for (String val : value.split(",")) {
                            ids.add(val);
                        }
                    } else {
                        // 单值
                        ids.add(value);
                    }
                }
            }

            exp = DynamicParse.buildSql(exp, (User) c.getSessionAttr(EovaConst.USER));

            EovaExp se = new EovaExp(exp);
            String select = se.simpleSelect;
            String where = se.where;
            String from = se.from;
            String pk = se.pk;
            String cn = se.cn;

            // 清除value列查询条件，防止干扰翻译SQL条件
            where = filterValueCondition(where, pk);
            // PS:底部main有测试用例

            StringBuilder sql = new StringBuilder();
            sql.append(select);
            sql.append(from);
            sql.append(SqlUtil.buildWhere(where));

            // 查询本次所有翻译值
            if (!xx.isEmpty(ids)) {
                sql.append(" and ").append(pk);
                sql.append(" in(");
                // 根据当前页数据value列查询外表name列
                for (String id : ids) {
                    // TODO There might be a SQL injection risk warning
                    sql.append(xx.format(id)).append(",");
                }
                sql.deleteCharAt(sql.length() - 1);
                sql.append(")");
            }

            // 缓存配置
            String cache = se.getPara(EovaExpParam.CACHE);
            List<Record> translates = null;
            if (xx.isEmpty(cache)) {
                translates = Db.use(se.ds).find(sql.toString());
            } else {
                translates = Db.use(se.ds).findByCache(cache, sql.toString(), sql.toString());
            }

            // 翻译匹配项
            for (Record re : reList) {
                Object o = re.get(en);
                // 空字段无法翻译
                if (o == null) {
                    re.set(en, "");
                    continue;
                }

                String value = o.toString();

                String text = "";
                if (value.contains(",")) {
                    // 多值
                    for (String val : value.split(",")) {
                        text += translateValue(pk, cn, translates, val);
                        text += ',';
                    }
                    text = xx.delEnd(text, ",");
                } else {
                    text = translateValue(pk, cn, translates, value);
                }
                re.set(en, text);
            }
        }
    }

    /**
     * 将value翻译text
     *
     * @param valueField 键列名
     * @param textField  值列名
     * @param translates 字典集合
     * @param value
     */
    public static String translateValue(String valueField, String textField, List<Record> translates, String value) {
        for (Record r : translates) {
            // 翻译前的值(默认为第1列查询值)
            String val = r.get(valueField).toString();
            // 翻译后的值(默认为第2列查询值)
            String txt = r.get(textField).toString();
            if (txt == null) {
                throw new RuntimeException("翻译后的值不能为Null,可能是脏数据,请修正!");
            }
            // 命中文案
            if (value.equals(val)) {

                // 国际化翻译
                String s = I18NBuilder.get(txt);
                if (!xx.isEmpty(s)) {
                    return s;
                }

                return txt;
            }
        }
        // 未命中返回值作为文案
        return value;
    }

    /**
     * 通过Form构建数据
     *
     * @param c      控制器
     * @param object    对象属性
     * @param record 当前操作对象数据集
     * @param pkName 主键字段名
     * @return 视图表数据分组
     */
    public static Map<String, Record> buildData(Controller c, MetaObject object, Record record, String pkName, boolean isInsert) {
        Map<String, Record> datas = new HashMap<String, Record>();

        for (MetaField f : object.getFields()) {
            // 跳过禁用字段
            if (isInsert) {
                if (f.getInt("add_status") == 50) {
                    continue;
                }
            } else {
                if (f.getInt("update_status") == 50) {
                    continue;
                }
            }

            // 字段名
            String key = f.getEn();
            Object value = c.getPara(key);
            // 预处理
            value = TemplateUtil.buildValue(f, value);
            // 类型转换
            value = EovaConfig.convertor.convert(f, value);

            // 全量的数据
            record.set(key, value);
        }
        return datas;
    }

    /**
     * 视图持久化操作(暂时废弃)
     *
     * @param mode   操作模式 add/update
     * @param object 元对象
     * @param data   视图当前操作所有数据
     */
    @Deprecated
    @SuppressWarnings("rawtypes")
    public static void operateView(String mode, MetaObject object, Record data) {
        MetaObjectConfig config = object.getConfig();
        LinkedHashMap<String, TableConfig> view = config.getView();
        Iterator iter = view.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String tableName = (String) entry.getKey();
            TableConfig tc = (TableConfig) entry.getValue();

            // 当前表的条件字段
            String whereField = tc.getWhereField();
            // 当前表的条件字段对应字段
            String paramField = tc.getParamField();

            // 按表获取持久化对象
            // Record po = recordGroup.get(table);
            Record po = getRecordByTableName(object, data, tableName);
            if (po == null) {
                continue;
            }

            // 主键可能并不是显示字段，所以需要手工获取关联字段的值
            if (!po.getColumns().containsKey(whereField)) {
                po.set(whereField, data.get(paramField));
            }

            if (mode.equals(TemplateConfig.UPDATE)) {
                // 按本表条件的关联字段进行更新
                Db.use(object.getDs()).update(tableName, whereField, po);
            } else if (mode.equals(TemplateConfig.ADD)) {
                // 按本表条件的关联字段进行更新
                Db.use(object.getDs()).save(tableName, po);
            }
        }
    }

    /**
     * 根据表名从当前操作数据中构建该表所属字段数据
     *
     * @param object    元数据
     * @param data      当前操作数据集
     * @param tableName 当前持久化表名
     * @return 当前持久化数据对象
     */
    private static Record getRecordByTableName(MetaObject object, Record data, String tableName) {
        List<MetaField> fields = object.getFields();
        Record r = null;
        for (MetaField f : fields) {
            if (tableName.equals(f.getStr("table_name"))) {
                String en = f.getEn();
                Object val = data.get(en);
                if (val == null)
                    continue;
                if (r == null)
                    r = new Record();
                r.set(en, val);
            }
        }
        return r;
    }

    /**
     * 过滤指定查询条件
     *
     * @param where   查询条件Sql
     * @param colName 要过滤的列名
     * @return 过滤后的Sql
     */
    public static String filterValueCondition(String where, String colName) {
        if (where.contains(colName)) {
            where = where.replaceAll("( " + colName + ".*?)and", "");
        }
        return where;
    }

    /**
     * 获取关联参数
     *
     * @param c
     * @return
     */
    public static Record getRef(Controller c) {
        Record r = new Record();

        try {
            String ref = c.getPara("ref");
            if (xx.isEmpty(ref)) {
                return r;
            }
            String[] fields = ref.split(",");
            for (String field : fields) {
                String[] strs = field.split(":");
                r.set(strs[0], strs[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return r;
        }

        return r;
    }

    /**
     * 构建关联参数值
     *
     * @param object
     */
    public static void buildRef(Controller ctrl, MetaObject object) {
        for (MetaField ei : object.getFields()) {
            String key = ei.getEn();

            Record ref = getRef(ctrl);
            if (ref != null && !xx.isEmpty(ref.get(key))) {
                ei.put("value", ref.get(key));
                ei.put("is_disable", true);
            }
        }
    }

    public static void main(String[] args) {
        /*
         * 清除value列查询条件，防止干扰翻译SQL条件 自定义SQL eg. where uid = ${user.id} and type = 3 自动翻译SQL eg. and uid in(1,2,3)
         */
        // String where = "where uid = ${user.id} and type = 1 and state = 1";
        // String where = "where type = 2 and uid = ${user.id} and state = 2";
        String where = "where type = 3 and uid = ${user.id} and state = 3 and uid = 3 and uid in (1,2,3)";
        String pk = "uid";
        System.out.println(where);
        where = filterValueCondition(where, pk);
        System.out.println(where);

        // addwhere
        System.out.println(addWhere(new EovaExp("select * from users where 1=1"), "id = 1"));
        System.out.println(addWhere(new EovaExp("select id ID,name CN from users where a = 1"), "id = 1"));
    }

    /**
     * 向上递归查找父节点数据
     *
     * @param treeConfig 树配置
     * @param view       数据源表名
     * @param pk         主键列
     * @param list       显示结果数据
     * @param parents    待查找数据
     */
    public static void findParent(TreeConfig treeConfig, String ds, String select, String view, String pk, List<Record> list, List<Record> parents) {
        Set<String> pids = new HashSet<>();
        for (Record x : parents) {
            pids.add(x.get(treeConfig.getParentField()).toString());
        }
        if (!xx.isEmpty(pids)) {
            if (pids.size() == 1 && pids.toArray()[0].equals(treeConfig.getRootPid())) {
                // 到根节点停止递归
                return;
            }
            parents = Db.use(ds).find(String.format("%s from %s where %s in (%s)", select, view, pk, xx.join(pids, "'", ",")));

            // 父节点去重
            for (Record p : parents) {
                boolean b = false;
                for (Record x : list) {
                    if (x.get(treeConfig.getIdField()).toString().equals(p.get(treeConfig.getIdField()).toString())) {
                        b = true;
                        break;
                    }
                }
                if (!b)
                    list.add(p);
            }
            findParent(treeConfig, ds, select, view, pk, list, parents);
        }
    }

    /**
     * 剥离虚拟字段
     *
     * @param e 待剥离数据集
     * @return 虚拟字段数据
     */
    public static Record peelVirtual(Record e) {
        Record t = new Record();
        // 移动虚拟字段
        String[] cols = e.getColumnNames();
        for (String s : cols) {
            if (s.startsWith("v_")) {
                t.set(s, e.get(s));
                e.remove(s);
            }
        }
        return t;
    }
}