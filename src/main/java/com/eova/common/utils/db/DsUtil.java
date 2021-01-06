/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.common.utils.db;

import com.alibaba.druid.stat.TableStat.Name;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eova.common.utils.xx;
import com.eova.config.EovaConfig;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Config;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.Record;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * 数据源工具类
 *
 * @author Jieven
 * @date 2015-6-27
 */
public class DsUtil {

    public static final String TABLE = "Table";
    public static final String VIEW = "View";

    /**
     * 获得元数据对象
     *
     * @param ds    数据源
     * @param props 连接配置
     * @return
     */
    public static DatabaseMetaData getDatabaseMetaData(String ds, Properties props) {
        Connection conn = null;
        try {
            Config config = DbKit.getConfig(ds);
            if (config == null) {
                throw new SQLException(ds + " datasrouce can not get config");
            }
            conn = config.getDataSource().getConnection();
            // TODO Mysql Test is OK!
            if (props != null) {
                conn.setClientInfo(props);
            }
            DatabaseMetaData md = conn.getMetaData();
            return md;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeConn(conn);
        }
    }

    public static DatabaseMetaData getDatabaseMetaData(String ds) {
        return getDatabaseMetaData(ds, null);
    }

    /**
     * 获取数据源的数据库名
     *
     * @param ds 数据源
     * @return
     */
    public static String getDbNameByConfigName(String ds) {
        try {
            Config config = DbKit.getConfig(ds);
            if (config == null) {
                throw new SQLException(ds + " datasrouce can not get config");
            }
            Connection conn = config.getDataSource().getConnection();
            if (conn == null) {
                return null;
            }
            return conn.getCatalog();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取数据源的用户名
     *
     * @param ds 数据源
     * @return
     */
    public static String getUserNameByConfigName(String ds) {
        try {
            DatabaseMetaData databaseMetaData = getDatabaseMetaData(ds);
            return databaseMetaData.getUserName();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取数据源中表/视图的名字列表
     *
     * @param ds               数据源
     * @param type             DsUtil.TABLE/VIEW
     * @param tableNamePattern TODO
     * @return
     */
    public static List<String> getTableNamesByConfigName(String ds, String type, String tableNamePattern) {

        if (tableNamePattern == null) {
            tableNamePattern = "%";
        }

        List<String> tables = new ArrayList<String>();
        ResultSet rs = null;
        try {
            DatabaseMetaData md = getDatabaseMetaData(ds);
            rs = md.getTables(null, getSchemaPattern(ds), tableNamePattern, new String[]{type.toUpperCase()});
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
        }
        return tables;
    }

    public static String getSchemaPattern(String ds) {
        String dbType = EovaConfig.getDbType(ds);
        String dbName = getDbNameByConfigName(ds);

        String schemaPattern = null;
        if (dbType.equals(JdbcConstants.MYSQL)) {
            // MYSQL 按DB名区分
            schemaPattern = dbName;
        } else if (dbType.equals(JdbcConstants.ORACLE)) {
            // Oracle 按用户名区分
            return getUserNameByConfigName(ds).toUpperCase();
        }

        return schemaPattern;

        //		if (DATABASETYPE.ORACLE.equals(dbtype)) {
        //			schemaPattern = username;
        //			if (null != schemaPattern) {
        //				schemaPattern = schemaPattern.toUpperCase();
        //			}
        //		} else if (DATABASETYPE.MYSQL.equals(dbtype)) {
        //			// Mysql查询
        //			// MySQL 的 table 这一级别查询不到备注信息
        //			schemaPattern = dbname;
        //			rs = meta.getTables(catalog, schemaPattern, tableNamePattern, types);
        //		} else if (DATABASETYPE.SQLSERVER.equals(dbtype) || DATABASETYPE.SQLSERVER2005.equals(dbtype)) {
        //			// SqlServer
        //			tableNamePattern = "%";
        //			rs = meta.getTables(catalog, schemaPattern, tableNamePattern, types);
        //		} else if (DATABASETYPE.DB2.equals(dbtype)) {
        //			// DB2查询
        //			schemaPattern = "jence_user";
        //			tableNamePattern = "%";
        //			rs = meta.getTables(catalog, schemaPattern, tableNamePattern, types);
        //		} else if (DATABASETYPE.INFORMIX.equals(dbtype)) {
        //			// SqlServer
        //			tableNamePattern = "%";
        //			rs = meta.getTables(catalog, schemaPattern, tableNamePattern, types);
        //		} else if (DATABASETYPE.SYBASE.equals(dbtype)) {
        //			// SqlServer
        //			tableNamePattern = "%";
        //			rs = meta.getTables(catalog, schemaPattern, tableNamePattern, types);
        //		} else {
        //			throw new RuntimeException("不认识的数据库类型!");
        //		}
    }

    public static String getPkName(String ds, String table) {
        ResultSet rs = null;
        try {
            String schemaPattern = null;
            if (xx.isOracle()) {
                schemaPattern = getUserNameByConfigName(ds);
            }
            DatabaseMetaData md = getDatabaseMetaData(ds);
            rs = md.getPrimaryKeys(null, schemaPattern, table);
            if (rs == null) {
                return null;
            }
            while (rs.next()) {
                return rs.getString("COLUMN_NAME");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
        }
        return null;
    }

    /**
     * 自动识别DB类型,进行大小写转换
     *
     * @param ds 数据源
     * @param s  待转换字符串
     * @return
     */
    public static String autoCase(String ds, String s) {
        String dbType = EovaConfig.getDbType(ds);

        if (dbType.equals(JdbcConstants.MYSQL) || dbType.equals(JdbcConstants.POSTGRESQL)) {
            return s.toLowerCase();
        }
        if (dbType.equals(JdbcConstants.ORACLE)) {
            return s.toUpperCase();
        }
        // JdbcConstants.SQL_SERVER;可混写
        return s;
    }

    public static JSONArray getColumnInfoByConfigName(String ds, String tableNamePattern) {
        return getColumnInfoByConfigName(ds, tableNamePattern, false);
    }

    /**
     * 获取数据源中元数据Column信息
     *
     * @param ds               数据源
     * @param tableNamePattern 表名
     * @param isView           是否为视图
     * @return
     */
    public static JSONArray getColumnInfoByConfigName(String ds, String tableNamePattern, boolean isView) {
        // G001 根据DB类型 进行大小写敏感的处理
        tableNamePattern = autoCase(ds, tableNamePattern);

        JSONArray array = new JSONArray();
        ResultSet rs = null;
        try {
            Properties props = null;
            if (xx.isMysql()) {
                props = new Properties();
                props.setProperty("REMARKS", "true");// 获取注释
                props.setProperty("COLUMN_DEF", "true");// 获取默认值
            }
            DatabaseMetaData md = getDatabaseMetaData(ds, props);

            rs = md.getColumns(null, getSchemaPattern(ds), tableNamePattern, null);

            // 因为 Oralce 配置参数 DatabaseMetaData 无法获取注释，手工从表中查询字段注释, 一切都是因为Oracle没按JDBC套路出牌
            List<Record> comments = new ArrayList<>();
            if (xx.isOracle()) {
                try {
                    // 获取用户名 String userName = DsUtil.getUserNameByConfigName(ds);  TODO DS 已经相当于owner 条件了 废弃掉 待稳定后删除
                    if (isView) {
                        // 获取视图的SQL
                        String viewSql = Db.use(ds).queryStr("select text from user_views where view_name = ?", tableNamePattern);
                        if (xx.isEmpty(viewSql)) {
                            throw new RuntimeException("视图不存在,请检查视图名称是否正确,Oracle视图必须大写:" + tableNamePattern);
                        }
                        // 获取视图里的表名
                        Set<Name> tableNames = SqlUtil.getTableNames(JdbcConstants.ORACLE, viewSql);
                        // 把当前视图所用表的注释都获取到
                        for (Name name : tableNames) {
                            String sql = "select column_name,comments from user_col_comments where table_name = ?";
                            comments.addAll(Db.use(ds).find(sql, name.getName()));
                        }
                    } else {
                        // 获取当前表的所有字段的注释
                        String sql = "select column_name,comments from user_col_comments where table_name = ?";
                        comments = Db.use(ds).find(sql, tableNamePattern);
                    }
                } catch (Exception e) {
                    LogKit.error("尝试读取Oracle字段注释发生异常", e);
                }
            }

            // 获取列数
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            // 遍历ResultSet中的每条数据
            while (rs.next()) {
                // System.out.println("Remarks: "+ rs.getObject(12));
                JSONObject json = new JSONObject();
                // 遍历每一列
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    String value = rs.getString(columnName);
                    json.put(columnName, value);
                }
                // oracle单独获取注释 TODO 元数据暂无，应该换Oracle驱动类中的方法获取
                if (xx.isOracle() && !xx.isEmpty(comments)) {
                    json.put("REMARKS", getOracleRemark(comments, json.getString("COLUMN_NAME")));
                }
                array.add(json);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
        }
        return array;
    }

    private static void closeConn(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignore) {
            }
        }
    }

    private static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignore) {
            }
        }
    }

    /**
     * 获取Oracle注释
     *
     * @param comments 表注释集合
     * @param en       字段名
     * @return
     */
    private static String getOracleRemark(List<Record> comments, String en) {
        for (Record x : comments) {
            if (en.equalsIgnoreCase(x.getStr("column_name"))) {
                return x.getStr("comments");
            }
        }
        return null;
    }
}