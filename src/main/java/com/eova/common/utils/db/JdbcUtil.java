/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.common.utils.db;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * JDBC工具类
 *
 * @author Jieven
 * @date 2014-9-12
 */
public class JdbcUtil {

    /**
     * 获取数据库连接,返回提示信息
     *
     * @param url
     * @param username
     * @param password
     * @return
     */
    public static String initConnection(String url, String username, String password) {
        // String oracleDrivers = "oracle.jdbc.driver.OracleDriver";
        String mysqlDrivers = "com.mysql.jdbc.Driver";
        System.setProperty("jdbc.drivers", mysqlDrivers);
        try {
            Class.forName(mysqlDrivers);
            Connection conn = DriverManager.getConnection(url, username, password);
            if (conn == null) {
                return "创建JDBC连接失败";
            }
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
    }

}