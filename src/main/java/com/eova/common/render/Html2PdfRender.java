/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.common.render;

import com.eova.common.utils.io.TxtUtil;
import com.eova.engine.DynamicParse;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import org.beetl.core.Template;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.HashMap;

public class Html2PdfRender extends Render {

    private final static String CONTENT_TYPE = "application/pdf;charset=" + getEncoding();

    private final String file;
    private final String fileName;

    /**
     * 渲染文件
     *
     * @param fileName 下载文件名
     * @param path     模版文件路径
     * @param paras    模版参数
     */
    public Html2PdfRender(String fileName, String path, HashMap<String, Object> paras) {
        this.fileName = fileName;
        this.file = parse(path, paras);
    }

    @Override
    public void render() {
        PrintWriter writer = null;
        try {
            response.setHeader("Pragma", "no-cache"); // HTTP/1.0 caches might not implement Cache-Control and might only implement Pragma: no-cache
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setDateHeader("Expires", 0);
            response.setContentType(CONTENT_TYPE);
            response.setCharacterEncoding(getEncoding());

            writer = response.getWriter();
            writer.write(file);
            writer.flush();
        } catch (IOException e) {
            throw new RenderException(e);
        } finally {
            if (writer != null)
                writer.close();
        }
    }


    public String parse(String path, HashMap<String, Object> params) {
        String temp = TxtUtil.getTxt(path);
        Template t = DynamicParse.gt.getTemplate(temp);
        for (String key : params.keySet()) {
            Object o = params.get(key);
            t.binding(key, o);
        }
        return t.render();
    }

}