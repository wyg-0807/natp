/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.config;

import com.eova.common.utils.db.DbUtil;
import com.eova.common.utils.io.FileUtil;
import com.eova.common.utils.io.NetUtil;
import com.eova.common.utils.io.ZipUtil;
import com.eova.common.utils.xx;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PathKit;

import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class EovaInit {

    /**
     * 异步初始化JS插件包<br>
     * 1.通过网络自动下载plugins.zip <br>
     * 2.解压到webapp/plugins/ <br>
     * 3.删除下载临时文件 <br>
     */
    public static void initPlugins() {
        // 异步下载插件包
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    // 下载到Web根目录
                    String zipPath = EovaConst.DIR_WEB + "plugins.zip";

                    if (!FileUtil.isExists(EovaConst.DIR_PLUGINS)) {
                        System.err.println("正在下载：" + EovaConst.PLUGINS_URL);
                        NetUtil.download(EovaConst.PLUGINS_URL, zipPath);

                        System.err.println("开始解压：" + zipPath);
                        ZipUtil.unzip(zipPath, EovaConst.DIR_PLUGINS, null);
                        System.err.println("已解压到：" + EovaConst.DIR_PLUGINS);

                        FileUtil.delete(zipPath);
                        System.err.println("清理下载Zip");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    /**
     * 初始化输出Oracle脚本
     */
    public static void initCreateSql() {
        // 异步下载插件包
        Thread t = new Thread() {
            @Override
            public void run() {
                System.out.println("正在生成 eova oracle sql ing...");
                DbUtil.createOracleSql(xx.DS_EOVA, "EOVA%");

                System.out.println();
                System.out.println();

                System.out.println("正在生成 main oracle sql ing...");
                DbUtil.createOracleSql(xx.DS_MAIN, "%");
            }
        };
        t.start();
    }

    /**
     * 初始化静态配置
     */
    public static void initStatic() {
//		xx.getConfig("upload_type")
    }

    /**
     * 初始化配置
     */
    public static void initConfig(Map<String, String> props) {
        String resPath = PathKit.getRootClassPath() + File.separator;
        // 加载默认配置
        boolean flag = loadConfig(props, resPath + "default");
        if (flag) {
            LogKit.info("默认配置加载成功:(resources/default)\n");
        }
        // 加载本地配置
        flag = loadConfig(props, resPath + "dev");
        if (flag) {
            LogKit.info("开发配置覆盖成功:(resources/dev)\n");
        }
        try {
            // 加载运行环境配置
            String envConfigPath = xx.getConfig("env_config_path");
            if (!xx.isEmpty(envConfigPath)) {
                flag = loadConfig(props, envConfigPath);
                if (flag) {
                    LogKit.info(String.format("环境配置覆盖成功:%s\n", envConfigPath));
                }
            }
        } catch (Exception e) {
            LogKit.error(String.format("加载环境配置异常:%s", e.getMessage()));
        }

    }

    /**
     * 加载配置
     *
     * @param path
     * @return
     */
    private static boolean loadConfig(Map<String, String> props, String path) {
        if (!FileUtil.isDir(path)) {
            return false;
        }
        File[] files = FileUtil.getFiles(path);
        for (File file : files) {
            if (!file.getName().endsWith(".config")) {
                continue;
            }
            Properties properties = FileUtil.getProp(file);
            Set<Object> keySet = properties.keySet();
            for (Object ks : keySet) {
                String key = ks.toString();
                props.put(key, properties.getProperty(key));
            }
            LogKit.info(file.getName());
        }
        return true;
    }

}