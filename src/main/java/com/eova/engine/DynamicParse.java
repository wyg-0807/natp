/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.engine;

import com.eova.common.utils.xx;
import com.eova.config.EovaConfig;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;

import java.io.IOException;

/**
 * Eova表达式动态解析器
 * 变量+逻辑运算
 *
 * @author Jieven
 */
public class DynamicParse {

    public static GroupTemplate gt;

    static {
        if (gt == null) {
            StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
            Configuration cfg = null;
            try {
                cfg = Configuration.defaultConfiguration();
            } catch (IOException e) {
                e.printStackTrace();
            }
            gt = new GroupTemplate(resourceLoader, cfg);
        }
    }

    public static String buildSql(String exp, Object... params) {
        if (xx.isEmpty(exp) || xx.isEmpty(params)) {
            return exp;
        }
        exp = exp.replace("\n", "");
        exp = exp.replace("\r", "");
        exp = exp.replace("\t", " ");

        Template t = gt.getTemplate(exp);
        for (Object o : params) {
            t.binding(o.getClass().getSimpleName().toLowerCase(), o);
        }

        exp = t.render();

        if (EovaConfig.isDevMode) {
            // System.out.println("EovaExp Build:" + exp);
        }

        return exp;
    }

}