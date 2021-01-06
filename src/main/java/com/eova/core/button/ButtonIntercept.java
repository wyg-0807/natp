/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.core.button;

import com.eova.aop.AopContext;
import com.eova.aop.MetaObjectIntercept;
import com.eova.model.RoleBtn;

public class ButtonIntercept extends MetaObjectIntercept {

    @Override
    public String deleteBefore(AopContext ac) throws Exception {
        int id = ac.record.getInt("id");

        // 删除菜单按钮关联权限
        RoleBtn.dao.deleteByBid(id);

        return null;
    }

}