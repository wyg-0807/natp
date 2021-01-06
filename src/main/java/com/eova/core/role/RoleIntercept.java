/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.core.role;

import com.eova.aop.AopContext;
import com.eova.aop.MetaObjectIntercept;

public class RoleIntercept extends MetaObjectIntercept {

    @Override
    public String addBefore(AopContext ac) throws Exception {
        Integer lv = ac.record.getInt("lv");
        Integer roleLv = ac.user.getRole().getInt("lv");
        if (lv <= roleLv) {
            return error("权限级别必须大于：" + roleLv);
        }
        return null;
    }

    @Override
    public String updateBefore(AopContext ac) throws Exception {
        return addBefore(ac);
    }


}