/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.template.single;

import com.eova.aop.AopContext;
import com.eova.common.utils.io.FileUtil;
import com.eova.config.EovaConst;
import com.eova.core.menu.config.MenuConfig;
import com.eova.i18n.I18NBuilder;
import com.eova.model.*;
import com.eova.service.sm;
import com.eova.template.common.util.TemplateUtil;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.upload.UploadFile;
import org.jetbrains.annotations.TestOnly;

import java.util.List;

/**
 * 业务模版：单表(DataGrid)
 *
 * @author Jieven
 */

public class SingleController extends Controller {

    final Controller ctrl = this;

    /**
     * 自定义拦截器
     **/
    protected SingleIntercept intercept = null;

    public void list() {

        String menuCode = this.getPara(0);
        // 获取元数据
        Menu menu = Menu.dao.findByCode(menuCode);
        MenuConfig config = menu.getConfig();
        String objectCode = config.getObjectCode();
        MetaObject object = MetaObject.dao.getByCode(objectCode);
        if (object == null) {
            throw new RuntimeException("元对象不存在,请检查是否存在?元对象编码=" + objectCode);
        }

        // 根据权限获取功能按钮
        User user = this.getSessionAttr(EovaConst.USER);
        List<Button> btnList = Button.dao.queryByMenuCode(menuCode, user.getRid());

        // 是否需要显示快速查询
        setAttr("isQuery", MetaObject.dao.isExistQuery(objectCode));

        setAttr("menu", menu);
        setAttr("btnList", btnList);
        setAttr("object", object);

        render("/eova/template/single/list.html");
    }

    public void importXls() {
        String menuCode = this.getPara(0);
        setAttr("menuCode", menuCode);
        render("/eova/template/single/dialog/import.html");
    }

    public void doImportXls() throws Exception {

        String menuCode = this.getPara(0);

        // 获取元数据
        Menu menu = Menu.dao.findByCode(menuCode);
        MenuConfig config = menu.getConfig();
        String objectCode = config.getObjectCode();
        final MetaObject object = sm.meta.getMeta(objectCode);

        intercept = TemplateUtil.initIntercept(menu.getBizIntercept());

        // 默认上传到/temp 临时目录
        final UploadFile file = getFile("upfile", "/temp");
        if (file == null) {
            uploadCallback(false, I18NBuilder.get("上传失败，文件不存在"));
            return;
        }

        // 获取文件后缀
        String suffix = FileUtil.getFileType(file.getFileName());
        if (!suffix.equals(".xls")) {
            uploadCallback(false, I18NBuilder.get("请导入.xls格式的Excel文件"));
            return;
        }

        // 事务(默认为TRANSACTION_READ_COMMITTED)
        SingleAtom atom = new SingleAtom(file.getFile(), object, intercept, ctrl);
        boolean flag = Db.use(object.getDs()).tx(atom);

        if (!flag) {
            atom.getRunExp().printStackTrace();
            uploadCallback(false, atom.getRunExp().getMessage());
            return;
        }

        // 记录导入日志
        EovaLog.dao.info(this, EovaLog.IMPORT, object.getStr("code"));

        // 导入成功之后
        if (intercept != null) {
            try {
                AopContext ac = new AopContext(ctrl, atom.getRecords());
                intercept.importSucceed(ac);
            } catch (Exception e) {
                e.printStackTrace();
                uploadCallback(false, e.getMessage());
                return;
            }
        }

        uploadCallback(true, I18NBuilder.get("导入成功"));
    }

    // ajax 上传回调
    public void uploadCallback(boolean succeed, String msg) {
        renderHtml("<script>parent.callback(\"" + msg + "\", " + succeed + ");</script>");
    }

}