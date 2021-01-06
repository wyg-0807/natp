/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.core.auth;

import com.eova.common.Easy;
import com.eova.common.utils.xx;
import com.eova.config.EovaConst;
import com.eova.model.*;
import com.eova.service.sm;
import com.eova.widget.WidgetUtil;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.activerecord.tx.TxConfig;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限管理
 *
 * @author Jieven
 * @date 2014-9-11
 */
public class AuthController extends Controller {

    /**
     * 菜单基本功能管理
     */
    public void toRoleChoose() {
        setAttr("rid", getPara(0));

        User user = getSessionAttr(EovaConst.USER);
        // 当前用户的下级角色
        List<Role> roles = Role.dao.findSubRole(user.getRole().getInt("lv"));
        setAttr("roles", roles);

        render("/eova/auth/roleChoose.html");
    }

    /**
     * 获取功能JSON
     */
    public void getFunJson() {
        // 查所有节点
        int rootId = 0;

        // 获取登录用户的角色
        User user = getSessionAttr(EovaConst.USER);
        int rid = user.getRid();

        // 获取所有菜单信息
        LinkedHashMap<Integer, Menu> allMenu = (LinkedHashMap<Integer, Menu>) sm.auth.getByParentId(rootId);
        // 根据角色获取已授权菜单Code
        List<String> authMenuCodeList = sm.auth.queryMenuCodeByRid(rid);

        // 获取已授权菜单
        LinkedHashMap<Integer, Menu> authMenu = new LinkedHashMap<Integer, Menu>();
        for (Map.Entry<Integer, Menu> map : allMenu.entrySet()) {
            Menu menu = map.getValue();
            // 未授权 也不是超级管理员
            if (!authMenuCodeList.contains(menu.getStr("code")) && !user.isAdmin()) {
                continue;
            }
            authMenu.put(map.getKey(), menu);
        }

        // 获取已授权子菜单的所有上级节点(若功能有授权，需要找到上级才能显示)
        LinkedHashMap<Integer, Menu> authParent = new LinkedHashMap<Integer, Menu>();
        for (Map.Entry<Integer, Menu> map : authMenu.entrySet()) {
            WidgetUtil.getParent(allMenu, authParent, map.getValue());
        }

        // 根节点不显示排除
        authParent.remove(rootId);

        // 将已授权的子菜单 放入 已授权 父菜单 Map
        // 顺序说明：父在前，子在后,子默认又是有序的
        authParent.putAll(authMenu);

        // 获取所有按钮信息
        List<Button> btns = Button.dao.find("select * from eova_button where is_hide = 0 order by menu_code,group_num,order_num");
        // 获取已授权功能点
        List<Integer> roleBtnIds = RoleBtn.dao.queryByRid(rid);

        // 构建菜单对应功能点 eg. [玩家管理] 口查询 口新增 口修改 口删除
        for (Map.Entry<Integer, Menu> map : authParent.entrySet()) {
            buildBtn(map.getValue(), btns, roleBtnIds, rid);
        }

        // Map 转 Tree Json
        String json = WidgetUtil.menu2TreeJson(authParent, rootId);

        renderJson(json);
    }

    /**
     * 构建可授权功能
     *
     * @param menu       当前菜单
     * @param btns       所有功能
     * @param roleBtnIds 已授权按钮ID
     */
    private void buildBtn(Menu menu, List<Button> btns, List<Integer> roleBtnIds, int rid) {
        String code = menu.getStr("code");
        if (code.equals(Menu.TYPE_DIR) || code.equals(Menu.TYPE_DIY)) {
            // 忽略自定义URL和目录
            return;
        }

        String btnId = "", btnName = "";
        for (Button btn : btns) {
            // 检查当前菜单的按钮
            if (btn.getStr("menu_code").equals(code)) {
                int bid = btn.getInt("id");
                String name = btn.getStr("name");

                // 不是超管 且 未授权 -> 忽略此按钮
                if (EovaConst.ADMIN_RID != rid && !xx.isContains(roleBtnIds, bid)) {
                    continue;
                }

                btnId += bid + ",";
                btnName += name + ",";
            }
        }
        if (xx.isEmpty(btnId)) {
            return;
        }

        menu.put("btnId", xx.delEnd(btnId, ","));
        menu.put("btnName", xx.delEnd(btnName, ","));
    }

    /**
     * 获取角色已分配功能JSON
     */
    public void getRoleFunJson() {
        int rid = getParaToInt(0);
        if (xx.isEmpty(rid)) {
            renderJson(new Easy("参数缺失!"));
            return;
        }
        List<Integer> list = RoleBtn.dao.queryByRid(rid);
        String json = JsonKit.toJson(list);
        System.out.println(json);
        renderJson(json);
    }

    /**
     * 授权
     */
    @Before(Tx.class)
    @TxConfig(xx.DS_EOVA)
    public void roleChoose() {
        int rid = getParaToInt(0);
        if (xx.isEmpty(rid)) {
            renderJson(new Easy("参数缺失!"));
            return;
        }
        // 获取选中功能点
        String checks = getPara("checks");
        if (xx.isEmpty(checks)) {
            renderJson(new Easy("请至少勾选一个功能点"));
            return;
        }

        String[] ids = checks.split(",");
        User user = getSessionAttr(EovaConst.USER);
        int userRid = user.getRid();
        // 如果不是超管角色进行授权操作,只允许分配已拥有的权限
        if (!user.isAdmin()) {
            List<Integer> list = RoleBtn.dao.queryByRid(userRid);
            for (String id : ids) {
                if (!xx.isContains(list, id)) {
                    renderJson(new Easy("禁止越权操作"));
                    return;
                }
            }
        }

        // 删除历史授权
        Db.use(xx.DS_EOVA).update("delete from eova_role_btn where rid = ?", rid);
        if (xx.isEmpty(checks)) {
            renderJson(new Easy());
            return;
        }

        HashMap<String, RoleBtn> map = new HashMap<>();
        // 当前勾选的授权
        for (String id : ids) {
            if (!map.containsKey(id)) {
                RoleBtn x = new RoleBtn();
                x.set("rid", rid);
                x.set("bid", id);
                map.put(id, x);
            }
        }

        // 批量更新
        for (String key : map.keySet()) {
            RoleBtn rf = map.get(key);
            rf.remove("id");
            rf.set("rid", rid);
            rf.save();
        }

        renderJson(new Easy());
    }

}