/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.common.utils.db;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eova.common.utils.io.TxtUtil;
import com.eova.common.utils.xx;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DbUtil {

    private static String convertDataType(JSONObject o) {
        // 字段类型(大写)
        String type = o.getString("TYPE_NAME").toUpperCase();
        // 字段长度
        int size = o.getIntValue("COLUMN_SIZE");
        int decimal = o.getIntValue("DECIMAL_DIGITS");

        // 纠错
        if (size == 0) {
            size = 1;
        } else if (size > 4000) {
            size = 4000;
        }

        String dataType = null;

        if (type.contains("INT")) {
            dataType = String.format("NUMBER(%d)", size - 1);// 获取最兼容的长度
        } else if (type.equals("FLOAT")) {
            dataType = String.format("NUMBER(%d,%d)", size, decimal);
        } else if (type.equals("DOUBLE")) {
            dataType = String.format("NUMBER(%d,%d)", size, decimal);
        } else if (type.equals("DECIMAL")) {
            dataType = String.format("NUMBER(%d,%d)", size, decimal);
        } else if (type.equals("BIT")) {
            dataType = "CHAR(1)";
        } else if (type.equals("CHAR")) {
            dataType = String.format("CHAR(%d)", size);
        } else if (type.indexOf("DATE") != -1) {
            dataType = "DATE";
        } else if (type.indexOf("TIME") != -1) {
            dataType = "TIMESTAMP(6)";
        } else {
            dataType = String.format("VARCHAR2(%d)", size);
        }

        return dataType;
    }

    public static void createOracleSql(String ds, String tableNamePattern) {

        StringBuilder sbs = new StringBuilder();
        StringBuilder sbDrop = new StringBuilder();
        StringBuilder sbDropSeq = new StringBuilder();
        StringBuilder sbCreateSeq = new StringBuilder();

        List<String> tables = DsUtil.getTableNamesByConfigName(ds, DsUtil.TABLE, tableNamePattern);

        for (String table : tables) {

            String pk = DsUtil.getPkName(ds, table);

            // 获取表中最大值
            String sql = "select max(" + pk + ") from " + table;
            Object max = Db.use(ds).queryColumn(sql);
            // 如果不是数字说明是UUID,不生成序列
            if (xx.isNum(max)) {

                String drop = "drop table " + table + ";\n";
                sbDrop.append(drop);

                String dropSeq = "drop sequence seq_" + table + ";\n";
                sbDropSeq.append(dropSeq);

                if (xx.isEmpty(max))
                    max = 0;
                String createSeq = "create sequence seq_" + table + " increment by 1 start with " + max + 1 + " maxvalue 9999999999;\n";
                sbCreateSeq.append(createSeq);
            }

            JSONArray list = DsUtil.getColumnInfoByConfigName(ds, table);

            StringBuilder sb = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            StringBuilder sb3 = new StringBuilder();

            // System.out.println(list);

            sb.append("create table " + table);
            sb.append("(\n");

            for (int i = 0; i < list.size(); i++) {
                JSONObject o = list.getJSONObject(i);

                Record re = new Record();
                re.set("en", o.getString("COLUMN_NAME"));
                re.set("cn", o.getString("REMARKS"));
                re.set("order_num", o.getIntValue("ORDINAL_POSITION"));
                re.set("is_required", !"YES".equalsIgnoreCase(o.getString("IS_NULLABLE")));

                // 是否自增
                boolean isAuto = "YES".equalsIgnoreCase(o.getString("IS_AUTOINCREMENT"));
                re.set("is_auto", isAuto);

                // 默认值
                String def = o.getString("COLUMN_DEF");
                re.set("defaulter", def);

                String dataType = convertDataType(o);
                // Mysql类型->Oracle类型
                sb.append(String.format("    %s %s", re.getStr("en"), dataType));
                if (re.getBoolean("is_required")) {
                    sb.append(" NOT NULL");
                }
                sb.append(",\n");

                // create remarks
                String remarks = o.getString("REMARKS");
                if (!xx.isEmpty(remarks)) {
                    String str = "comment on column %s.%s is '%s';\n";
                    sb2.append(String.format(str, table, re.getStr("en"), remarks));
                }

                // add default
                {
                    if (!xx.isEmpty(def)) {
                        String str = "alter table %s modify %s default %s;\n";
                        if (def.equals("CURRENT_TIMESTAMP")) {
                            sb3.append(String.format(str, table, re.getStr("en"), "sysdate"));
                        } else {
                            if (!dataType.contains("NUMBER")) {
                                def = xx.format(def);
                            }
                            sb3.append(String.format(str, table, re.getStr("en"), def));
                        }
                    }

                }

            }
            sb.delete(sb.length() - 2, sb.length() - 1);
            sb.append(");\n");

            // 导入元字段
            // importMetaField(code, list);

            // 导入视图默认第一列为主键
            String pkName = DsUtil.getPkName(ds, table);
            if (!xx.isEmpty(pkName)) {
                String str = "\nalter table %s add constraint pk_%s primary key(%s);\n";
                sb2.insert(0, String.format(str, table, table, pkName));
            }

            // 导入元对象
            // importMetaObject(ds, type, table, name, code, pkName);

            sbs.append(sb);
            sbs.append(sb2);
            sbs.append(sb3);
            sbs.append("\n");
        }

        System.out.println(sbDrop.toString());
        System.out.println(sbDropSeq.toString());
        System.out.println(sbCreateSeq.toString());
        System.out.println(sbs.toString());
    }

    /**
     * 格式化Oracle Date
     *
     * @param value
     * @return
     */
    public static String buildDateValue(Object value) {
        return "to_date('" + value + "','yyyy-mm-dd HH24:MI:SS')";
    }

    /**
     * 将数据变成Mysql插入脚本
     *
     * @param list  待生成数据集
     * @param table 表名
     * @param auto  自增列
     * @param sb
     */
    public static void generateSql(List<Record> list, String table, String auto, StringBuilder sb) {
        Set<String> updatePid = new HashSet<>();
        for (Record r : list) {
            appendSql(table, auto, sb, r);
            if (table.equals("eova_menu")) {
                Integer pid = r.getInt("parent_id");
                if (pid == 0) {
                    continue;
                }
                // 查找当前父的Code
                String findPCode = "select code from eova_menu where id = ?";
                String pcode = Db.use(xx.DS_EOVA).queryStr(findPCode, pid);
                // 迁移后自动按Code 更新关系
                String sql = String.format("SET @pid := 3;SELECT id INTO @pid FROM eova_menu b WHERE b.CODE = '%s';UPDATE eova_menu m SET m.parent_id = @pid WHERE m.CODE = '%s'", pcode,
                        r.getStr("code"));
                if (xx.isOracle()) {
                    sql = String.format("UPDATE EOVA_MENU m SET m.parent_id = ( SELECT id FROM EOVA_MENU b WHERE b.CODE = '%s' ) WHERE  m.code = '%s'", pcode, r.getStr("code"));
                }
                updatePid.add(sql);
            }
        }

        sb.append("\n");

        for (String s : updatePid) {
            sb.append(s);
            sb.append(";\n");
        }
    }

    private static void appendSql(String table, String auto, StringBuilder sb, Record r) {
        sb.append("INSERT INTO " + table + " (");
        String[] names = r.getColumnNames();
        for (String n : names) {
            sb.append(format(n)).append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")  VALUES (");
        for (String n : names) {
            if (n.equals(auto)) {
                if (xx.isOracle()) {
                    sb.append(String.format("seq_%s.nextval,", table));
                }
            } else {
                sb.append(formatVal(r.get(n))).append(',');
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(");");
        sb.append("\n");
    }

    public static String format(String name) {
        if (xx.isMysql()) {
            return '`' + name + '`';
        }
        return name;
    }

    private static Object formatVal(Object o) {
        if (o == null) {
            return o;
        }
        String s = o.toString();
        if (s.equals("true")) {
            return 1;
        }
        if (s.equals("false")) {
            return 0;
        }
        // ps:regex 4*\ = java 2*\
        if (s.indexOf("\\") != -1) {
            // 自动转义 富文本中JS转义符(比如js代码)
            s = s.replaceAll("\\\\", "\\\\\\\\");
        }
        if (s.indexOf("'") != -1) {
            // 自动转义引号
            s = s.replaceAll("'", "\\\\'");
        }
        return xx.format(s);
    }

    /**
     * SQL表名比较,自动忽略符号,空格
     *
     * @param full  全称 db.table.id
     * @param table 简称 db.table
     * @return
     */
    public static boolean compareTable(String full, String tableName) {
        full = full.replace("`", "").replace(" ", "").toLowerCase();
        tableName = tableName.replace("`", "").replace(" ", "").toLowerCase();

        // 格式:table vs table
        if (full.equals(tableName)) {
            return true;
        }

        String[] ss = full.split("\\.");
        // 格式:table.filed vs table
        if (ss.length == 2) {
            return ss[0].equals(tableName);
        }
        // 格式:db.table.filed vs db.table
        if (ss.length == 3) {
            return (ss[0] + "." + ss[1]).equals(tableName);
        }

        return false;
    }

    /**
     * SQL字段比较,自动忽略符号,空格
     *
     * @param full  全称 db.table.filed
     * @param table 简称 db.table
     * @return
     */
    public static boolean compareField(String full, String fieldName) {
        full = full.replace("`", "").replace(" ", "").toLowerCase();
        fieldName = fieldName.replace("`", "").replace(" ", "").toLowerCase();

        // 格式:filed vs filed
        if (full.equals(fieldName)) {
            return true;
        }

        String[] ss = full.split("\\.");
        // 格式:table.filed vs filed
        if (ss.length == 2) {
            return ss[1].equals(fieldName);
        }
        // 格式:db.table.filed vs table.filed
        if (ss.length == 3) {
            return (ss[1] + "." + ss[2]).equals(fieldName) || ss[2].equals(fieldName);
        }

        return false;
    }

    public static void main(String[] args) {
//		{
//			boolean s = compare("`demo`.`hotel_stock`.`hotel_id`", "demo.hotel");
//			System.out.println(s);
//		}
        {
            boolean s = compareTable("`demo`.`hotel`.`id`", "demo.hotel");
            System.out.println(s);
        }
    }

    /**
     * 获取字段名
     * 例：`hotel`.`id`
     * 值：id
     *
     * @param full 库名.表名.字段名
     * @return
     */
    public static String getEndName(String full) {
        full = full.replace("`", "").replace(" ", "");

        if (full.contains(".")) {
            String[] ss = full.split("\\.");
            return ss[ss.length - 1];
        }
        return full;
    }

    public static String simplify(String full) {
        full = full.replace("`", "").replace(" ", "");
        return null;
    }

    /**
     * 读取 SQL 文件，获取 SQL 语句
     *
     * @param sqlFilePath SQL脚本文件
     * @return List<sql> 返回所有 SQL 语句的 List
     * @throws Exception
     */
    public static List<String> loadSql(String sqlFilePath) throws Exception {
        List<String> sqlList = new ArrayList<String>();

        try {
            String txt = TxtUtil.getTxt(sqlFilePath);

            String[] sqlArr = txt.split(";");
            for (int i = 0; i < sqlArr.length; i++) {
                // 屏蔽注释
                String sql = sqlArr[i].replaceAll("--.*", "").trim();
                if (!sql.equals("")) {
                    sqlList.add(sql);
                }
            }
            return sqlList;
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

}