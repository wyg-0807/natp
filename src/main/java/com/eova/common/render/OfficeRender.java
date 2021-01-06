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
import java.util.HashMap;

public class OfficeRender extends Render {

    private final static String CONTENT_TYPE_EXCEL = "application/msexcel;charset=" + getEncoding();
    private final static String CONTENT_TYPE_WROD = "application/msword;charset=" + getEncoding();
    private final static String CONTENT_TYPE_PDF = "application/pdf;charset=" + getEncoding();

    private final static String FILE_TYPE_XLS = "xls";
    private final static String FILE_TYPE_DOC = "doc";
    private final static String FILE_TYPE_PDF = "pdf";

    private final String file;
    private final String fileType;
    private final String fileName;

    /**
     * 渲染
     *
     * @param fileType 文件格式: xls/doc/pdf
     * @param fileName 下载文件名
     * @param path     模版文件路径
     * @param paras    模版参数
     */
    public OfficeRender(String fileType, String fileName, String path, HashMap<String, Object> paras) {
        this.fileType = fileType;
        this.fileName = fileName;
        this.file = RenderUtil.renderFile(path, paras);
    }

    @Override
    public void render() {
        PrintWriter writer = null;
        try {
            response.setHeader("Pragma", "no-cache"); // HTTP/1.0 caches might not implement Cache-Control and might only implement Pragma: no-cache
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(fileName, getEncoding()));
            response.setDateHeader("Expires", 0);
            if (fileType.equalsIgnoreCase(FILE_TYPE_XLS)) {
                response.setContentType(CONTENT_TYPE_EXCEL);
            } else if (fileType.equalsIgnoreCase(FILE_TYPE_DOC)) {
                response.setContentType(CONTENT_TYPE_WROD);
            } else if (fileType.equalsIgnoreCase(FILE_TYPE_PDF)) {
                response.setContentType(CONTENT_TYPE_PDF);
            }

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
}