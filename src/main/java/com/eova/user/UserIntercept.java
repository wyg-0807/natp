/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.user;

import com.eova.aop.AopContext;
import com.eova.aop.MetaObjectIntercept;
import com.eova.common.utils.EncryptUtil;
import com.eova.common.utils.xx;
import com.jfinal.plugin.activerecord.Db;

/**
 * 自定义用户管理拦截器
 *
 * @author Jieven
 */
public class UserIntercept extends MetaObjectIntercept {

    @Override
    public String addBefore(AopContext ac) throws Exception {
        // 数据服务端校验
        String loginId = ac.record.getStr("login_id");
        Long num = Db.use(xx.DS_EOVA).queryLong("select count(*) from eova_user where login_id = ?", loginId);
        if (num > 0) {
            return warn("帐号重复,请重新填写!");
        }

        // 新增时密码加密储存
        String str = ac.record.getStr("login_pwd");
        ac.record.set("login_pwd", EncryptUtil.getSM32(str));

        return null;
    }

}