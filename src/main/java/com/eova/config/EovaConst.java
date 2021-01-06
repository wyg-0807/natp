/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.config;

import com.eova.common.utils.xx;
import com.jfinal.kit.PathKit;

import java.io.File;

/**
 * 系统配置
 *
 * @author Jieven
 * @date 2014-5-15
 */
public class EovaConst {
    /**
     * 默认 数据源名称
     **/
    public static final String DS_MAIN = "main";
    /**
     * EOVA 数据源名称
     **/
    public static final String DS_EOVA = "eova";
    /**
     * Oracle 默认Sequence前缀
     **/
    public static final String SEQ_ = "seq_";

    /**
     * 登录用户标识
     **/
    public static final String USER = "user";

    /**
     * 本地语言标识
     **/
    public static final String LOCAL = "local";

    /**
     * Cache Key 所有菜单信息
     **/
    public static final String ALL_MENU = "all_menu";

    /**
     * 默认超级管理员角色(创建菜单自动给角色授权)
     **/
    public static final int ADMIN_RID = xx.getConfigInt("admin_rid", 1);

    /**
     * WebApp 根目录
     **/
    public static final String DIR_WEB = PathKit.getWebRootPath() + File.separator;

    /**
     * 插件目录
     **/
    public static final String DIR_PLUGINS = PathKit.getWebRootPath() + File.separator + "plugins" + File.separator;

    /**
     * Eova插件包URL
     **/
    public static final String PLUGINS_URL = "http://7xign9.com1.z0.glb.clouddn.com/eova_plugins.zip";

    /**
     * 上传图片大小上限(单位:M)
     **/
    public static final int UPLOAD_IMG_SIZE = 10;

    /**
     * 虚拟字段标识
     **/
    public static final String VIRTUAL = "virtual";
}