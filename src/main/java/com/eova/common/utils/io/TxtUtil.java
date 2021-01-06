/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.common.utils.io;

import com.jfinal.core.JFinal;

import java.io.*;

public class TxtUtil {

    public final static int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public static String getTxt(String path) {
        File file = new File(path);
        StringBuilder result = new StringBuilder();
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), JFinal.me().getConstants().getEncoding());
            BufferedReader br = new BufferedReader(isr);
            String s = null;
            while ((s = br.readLine()) != null) {
                result.append(System.lineSeparator() + s);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public static String read(InputStream in) {
        InputStreamReader reader;
        try {
            reader = new InputStreamReader(in, JFinal.me().getConstants().getEncoding());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return read(reader);
    }

    public static String read(Reader reader) {
        try {

            StringWriter writer = new StringWriter();

            char[] buffer = new char[DEFAULT_BUFFER_SIZE];
            int n = 0;
            while (-1 != (n = reader.read(buffer))) {
                writer.write(buffer, 0, n);
            }

            return writer.toString();
        } catch (IOException ex) {
            throw new IllegalStateException("read error", ex);
        }
    }

}