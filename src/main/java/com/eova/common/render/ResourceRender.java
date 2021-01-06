/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.common.render;

import com.jfinal.kit.PathKit;
import com.jfinal.render.HtmlRender;

import java.util.HashMap;

public class ResourceRender extends HtmlRender {

    public ResourceRender(Object object, String view, HashMap<String, Object> attr) {
        super(RenderUtil.renderResource(buildResource(object, view), attr));
    }

    private static String buildResource(Object object, String filePath) {
        // 获取当前方法的上上级 也就是 调用
        // StackTraceElement[] ss = Thread.currentThread().getStackTrace();
        // StackTraceElement a = (StackTraceElement)ss[4];
        // String txt = Utils.readFromResource(filePath);
        String pack = PathKit.getPackagePath(object);
        return String.format("%s/resources/%s", pack, filePath);
    }

}