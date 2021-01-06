/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.common.utils.io;

import com.eova.common.utils.xx;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class FileUtil {

    /**
     * 允许上传的图片类型
     **/
    public static final String IMG_TYPE = ".jpeg|.jpg|.gif|.png|.bmp";
    /**
     * 允许上传的所有文件类型
     **/
    public static String ALL_TYPE = ".jpg|.jpeg|.gif|.png|.bmp|.gz|.7z|.rar|.zip|.swf|.mp3|.mp4|.jar|.apk|.ipa|.doc|.docx|.xls|.xlsx|.ppt|.pptx|.pdf|.txt";

    /**
     * 根据操作系统自动格式化Path
     *
     * @param path
     * @return
     */
    public static String formatPath(String path) {
        if (path.contains("/")) {
            path = path.replace("/", File.separator);
        }
        if (path.contains("\\")) {
            path = path.replace("/", File.separator);
        }
        return path;
    }

    /**
     * 检测文件大小
     *
     * @param file 文件
     * @param size 限制大小
     * @return true 超过限制
     */
    public static boolean checkFileSize(File file, int mb) {
        long size = file.length();
        return size > 1024 * 1024 * mb;
    }

    /**
     * 检查文件类型
     *
     * @param 文件名 @param isImg 是否检查图片 @return true=后缀合法 @throws
     */
    public static boolean checkFileType(String fileName, boolean isImg) {
        String fileType = getFileType(fileName);
        if (isImg) {
            return IMG_TYPE.indexOf(fileType.toLowerCase()) != -1;
        } else {
            return xx.getConfig("upload_type", ALL_TYPE).indexOf(fileType.toLowerCase()) != -1;
        }
    }

    /**
     * 获取文件类型
     *
     * @param fileName @throws
     */
    public static String getFileType(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * 文件是否存在
     *
     * @param path
     * @return
     */
    public static boolean isExists(String path) {
        File file = new File(path);
        return file.exists() && file.length() > 0;
    }

    /**
     * 目录是否存在
     *
     * @param path
     * @return
     */
    public static boolean isDir(String path) {
        File file = new File(path);
        return file.exists() && file.isDirectory();
    }

    /**
     * 删除文件
     *
     * @param path
     */
    public static void delete(String path) {
        delete(new File(path));
    }

    /**
     * 删除文件
     *
     * @param file
     */
    public static void delete(File file) {
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 重命名文件
     *
     * @param path
     * @param toPath
     * @throws Exception
     */
    public static void rename(String path, String toPath) throws IOException {
        File toBeRenamed = new File(path);
        // 检查要重命名的文件是否存在，是否是文件
        if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {
            throw new IOException("File does not exist: " + path);
        }

        File newFile = new File(toPath);
        if (toBeRenamed.renameTo(newFile) == false) {
            throw new IOException(String.format("文件重命令异常:%s -> %s", path, toPath));
        }
    }

    /**
     * 复制文件
     *
     * @param path
     * @param toPath
     * @throws Exception
     */
    public static void copy(String path, String toPath) throws IOException {
        FileInputStream ins = new FileInputStream(path);
        FileOutputStream out = new FileOutputStream(toPath);
        byte[] b = new byte[1024];
        int n = 0;
        while ((n = ins.read(b)) != -1) {
            out.write(b, 0, n);
        }

        ins.close();
        out.close();
    }

    /**
     * 获取目录下文件列表
     *
     * @param path 目录URL
     * @return
     */
    public static File[] getFiles(String path) {
        File file = new File(path);
        if (file.exists()) {
            return file.listFiles();
        }
        return null;
    }

    /**
     * 将文件转为属性
     *
     * @param file
     * @return
     */
    public static Properties getProp(File file) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            Properties properties = new Properties();
            properties.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties file.", e);
        } finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public static void main(String[] args) {
        {
            System.out.println(formatPath("C:\\static\\img" + "/product"));
            System.out.println(formatPath("C:/static/img" + "/product"));
            System.out.println(formatPath("home/static/img" + "/product"));
        }
    }

    /**
     * 将二进制数据写入文件
     *
     * @param data     二进制文件
     * @param fileDir  文件输出目录
     * @param fileName 文件输出名
     */
    public static void writeByteToFile(byte[] data, String fileDir, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(fileDir);
            if (!dir.exists() && dir.isDirectory()) {
                dir.mkdirs();
            }
            file = new File(fileDir + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}