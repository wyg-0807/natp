/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.service;

/**
 * 服务管理中心
 *
 * @author Jieven
 */
public class ServiceManager {
    /**
     * 权限服务
     **/
    public static AuthService auth;
    /**
     * 元服务
     **/
    public static MetaService meta;

    public static void init() {
        auth = new AuthService();
        meta = new MetaService();
    }
}