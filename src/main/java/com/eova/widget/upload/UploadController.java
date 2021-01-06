/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.widget.upload;

import com.eova.common.utils.io.FileUtil;
import com.eova.common.utils.io.ImageUtil;
import com.eova.common.utils.util.RandomUtil;
import com.eova.common.utils.xx;
import com.eova.config.EovaConfig;
import com.eova.config.EovaConst;
import com.eova.model.MetaField;
import com.eova.model.MetaFieldConfig;
import com.jfinal.core.Controller;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;

import java.io.File;
import java.util.List;

/**
 * 上传组件
 *
 * @author Jieven
 */
public class UploadController extends Controller {

    // 异步传图
    public void img() {

        String name = getPara("name");
        String fileDir = getPara("filedir");
        String fileName = getPara("filename");

        Ret r = upload("img", name, fileDir, fileName, xx.getConfigInt("upload_img_size", EovaConst.UPLOAD_IMG_SIZE));

        renderJson(r);
    }

    // 异步传文件
    public void file() {

        String name = getPara("name");
        String fileDir = getPara("filedir");
        String fileName = getPara("filename");

        // if (xx.isEmpty(filedir)) {
        // // 默认按日期划分目录
        // String today = DateTime.now().toString("yyyyMMdd");
        // filedir = File.separator + "upload" + File.separator + today;
        // }

        Ret rt = upload("file", name, fileDir, fileName, xx.getConfigInt("upload_size", 50));

        renderJson(rt);
    }

    // 异步传临时文件(用后应主动清理文件)
    public void temp() {
        String name = getPara("name");

        Ret rt = upload("file", name, xx.getConfig("eova_upload_temp", "/temp"), null, xx.getConfigInt("upload_size", 50));

        renderJson(rt);
    }

    // 编辑器上传图片(wangEditor)
    public void editor() {
        String domain = EovaConfig.getProps().get("domain_img");
        String editorDir = EovaConfig.getProps().get("dir_editor");

        // 获取自定义上传参数
        String objectCode = getPara("object_code");
        String en = getPara("en");
        MetaField field = MetaField.dao.getByObjectCodeAndEn(objectCode, en);
        MetaFieldConfig config = field.getConfig();

        // 自定义配置上传目录
        if (config != null && !xx.isEmpty(config.getFiledir())) {
            editorDir = field.getConfig().getFiledir();
        }

        if (xx.isEmpty(domain)) {
            throw new RuntimeException("图片上传异常,请先配置图片服务域名!配置项:domain.config domain_img=图片服务域名");
        }
        if (xx.isEmpty(editorDir)) {
            throw new RuntimeException("图片上传异常,请先配置图片服务目录!配置项:domain.config dir_editor=图片目录");
        }

        Ret rt = upload("img", null, editorDir, null, xx.getConfigInt("upload_size", 50));
        if (rt.isFail()) {
            renderText("error|" + rt.getStr("msg"));
            return;
        }
        renderText(String.format("%s/%s/%s", xx.delEnd(domain, "/"), xx.delStart(editorDir, "/"), rt.getStr("fileName")));
    }

    /**
     * 上传文件
     *
     * @param uploadType 上传类型:file/img
     * @param name       文件控件name
     * @param fileDir    文件上传目录(如为空,默认/temp)
     * @param fileName   文件名(如文件存在,覆盖之)
     * @param maxM       文件大小限制(M)
     * @return
     */
    private Ret upload(String uploadType, String name, String fileDir, String fileName, int maxM) {

        if (xx.isEmpty(fileDir)) {
            fileDir = "/temp";
        }

        UploadFile file = null;
        try {
            fileDir = FileUtil.formatPath(fileDir);
            if (name != null) {
                // 按参数名获取上传文件
                file = getFile(name, fileDir);
            } else {
                // 获取第一个上传文件
                List<UploadFile> files = getFiles(fileDir);
                if (xx.isEmpty(files)) {
                    return Ret.fail("msg", "请选择一个文件");
                }
                file = files.get(0);
            }
            if (xx.isEmpty(file)) {
                return Ret.fail("msg", "请选择一个文件");
            }
            if (FileUtil.checkFileSize(file.getFile(), maxM)) {
                return Ret.fail("msg", String.format("文件大小不能超过%sM", maxM));
            }
            // 图片合法性严格检查(图片后缀+图片头)
            if (uploadType.equalsIgnoreCase("img") && !ImageUtil.isImage(file.getFile().getPath())) {
                return Ret.fail("msg", "该文件不是标准的图片文件格式，请勿手工修改文件格式");
            }

            // 获取新文件名
            if (xx.isEmpty(fileName)) {
                // 获取文件名
                fileName = file.getFileName();
                // 获取文件后缀
                String suffix = FileUtil.getFileType(fileName);
                // 创建新的随机文件名(增量随机数防止并发重名异常)
                fileName = System.currentTimeMillis() + RandomUtil.nextIntAsStringByLength(5) + suffix;
            }

            // 新文件 Path
            String path = file.getUploadPath() + File.separator + fileName;

            /*
             * 如果文件存在,先删除.
             * 1.指定文件名,一般为覆盖文件,否则不应指定文件名!如不能覆盖应自定义上传逻辑
             * 2.未指定文件名,随机文件名,理论上不会存在重名,如巧合,应删之
             */
            if (FileUtil.isExists(path)) {
                FileUtil.delete(path);
            }

            FileUtil.rename(file.getFile().getPath(), path);
            LogKit.info(file.getFile().getPath() + " -> " + path);

        } catch (Exception e) {
            e.printStackTrace();
            return Ret.fail("msg", "系统异常：文件上传失败,请稍后再试");
        } finally {
            // 强制删除已上传的文件(如果还存在)
            FileUtil.delete(file.getFile());
        }
        return Ret.ok("fileName", fileName);
    }

}