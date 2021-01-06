/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.template.office;

import com.eova.common.Easy;
import com.eova.common.render.OfficeRender;
import com.eova.common.utils.xx;
import com.eova.config.EovaConst;
import com.eova.model.Button;
import com.eova.model.Menu;
import com.eova.model.User;
import com.eova.template.common.util.TemplateUtil;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;

import java.util.HashMap;
import java.util.List;

/**
 * 业务模版：Office
 *
 * @author Jieven
 */
public class OfficeController extends Controller {

    final Controller ctrl = this;

    /**
     * 自定义拦截器
     **/
    protected OfficeIntercept intercept = null;

    public void list() {
        String menuCode = this.getPara(0);

        // 获取元数据
        Menu menu = Menu.dao.findByCode(menuCode);

        // 根据权限获取功能按钮
        User user = this.getSessionAttr(EovaConst.USER);
        List<Button> btnList = Button.dao.queryByMenuCode(menuCode, user.getRid());

        setAttr("menu", menu);
        setAttr("btnList", btnList);

        // 参数传递
        String query = this.getRequest().getQueryString();
        setAttr("para", this.getPara() + (xx.isEmpty(query) ? "" : query));

        render("/eova/template/office/list.html");
    }

    // 显示文档
    public void show() throws Exception {
        String menuCode = this.getPara(0);

        Menu menu = Menu.dao.findByCode(menuCode);
        String url = menu.getStr("url");

        intercept = TemplateUtil.initIntercept(menu.getBizIntercept());

        HashMap<String, Object> data = new HashMap<>();

        if (intercept != null) {
            try {
                intercept.init(ctrl, data);
            } catch (Exception e) {
                renderText(e.getMessage());
                return;
            }
        }

        for (String key : data.keySet()) {
            Object o = data.get(key);
            setAttr(key, o);
        }

        render(url);
    }

    // 下载文档
    public void file() throws Exception {

        String menuCode = this.getPara(0);

        Menu menu = Menu.dao.findByCode(menuCode);
        String url = menu.getStr("url");

        // PathKit.getRootClassPath()
        String path = PathKit.getWebRootPath() + url;

        intercept = TemplateUtil.initIntercept(menu.getBizIntercept());

        HashMap<String, Object> data = new HashMap<>();

        String fileType = menu.getConfig().getParams().getString("office_type");
        String fileName = menu.getStr("name") + '.' + fileType;

        if (intercept != null) {
            try {
                intercept.init(ctrl, data);
            } catch (Exception e) {
                renderJson(new Easy(e.getMessage()));
                return;
            }
        }

        render(new OfficeRender(fileType, fileName, path, data));
    }

}