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

public class XmlRender extends Render {

    private static final String contentType = "text/xml;charset=" + getEncoding();

    private final String xml;

    public XmlRender(String xml) {
        if (xml == null)
            throw new IllegalArgumentException("The parameter xml can not be null.");
        this.xml = xml;
    }

    public void render() {
        PrintWriter writer = null;
        try {
            response.setHeader("Pragma", "no-cache"); // HTTP/1.0 caches might not implement Cache-Control and might only implement Pragma: no-cache
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType(contentType);
            response.setCharacterEncoding(getEncoding());

            writer = response.getWriter();
            writer.write(xml);
            writer.flush();
        } catch (IOException e) {
            throw new RenderException(e);
        } finally {
            if (writer != null)
                writer.close();
        }
    }
}