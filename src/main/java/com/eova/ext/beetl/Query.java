/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.ext.beetl;

import com.eova.common.utils.xx;
import com.eova.config.EovaConst;
import com.eova.model.MetaField;
import com.eova.model.User;
import com.eova.model.Widget;
import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.ext.web.SessionWrapper;

/**
 * 模版数据查询函数
 *
 * @author Jieven
 * @date 2014-5-23
 */
public class Query implements Function {
    @Override
    public Object call(Object[] paras, Context ctx) {
        String type = paras[0].toString();
        if (type.equals("fields")) {
            // 元字段查询
            String objectCode = paras[1].toString();
            if (xx.isEmpty(objectCode) && !objectCode.matches("\\w+")) {
                throw new RuntimeException("参数异常，请输入合法参数");
            }
            // 获取当前会话用户
            SessionWrapper session = (SessionWrapper) ctx.getGlobal("session");
            User user = null;
            if (session != null) {
                user = (User) session.get(EovaConst.USER);
            }
            return MetaField.dao.queryFieldsGroup(objectCode, user);
        } else if (type.equals("widget")) {
            // 自定义插件查询
            return Widget.dao.findByType(Widget.TYPE_DIY);
        }
        return null;
    }
}