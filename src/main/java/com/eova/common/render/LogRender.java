/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.common.render;

import com.jfinal.render.Render;
import com.jfinal.render.RenderException;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;

public class LogRender extends Render {

    private static final String contentType = "text/plain;charset=" + getEncoding();

    private final String fileName;
    private final String txt;

    public LogRender(String txt, String fileName) {
        if (txt == null)
            throw new IllegalArgumentException("The parameter txt can not be null.");
        this.txt = txt;
        this.fileName = fileName;
    }

    public void render() {
        PrintWriter writer = null;
        try {
            response.setHeader("Pragma", "no-cache"); // HTTP/1.0 caches might not implement Cache-Control and might only implement Pragma: no-cache
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType(contentType);
            response.setCharacterEncoding(getEncoding());
            response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(fileName, getEncoding()));

            writer = response.getWriter();
            writer.write(txt);
            writer.flush();
        } catch (IOException e) {
            throw new RenderException(e);
        } finally {
            if (writer != null)
                writer.close();
        }
    }
}