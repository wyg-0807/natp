/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.core.object;

import com.eova.aop.AopContext;
import com.eova.aop.MetaObjectIntercept;
import com.eova.model.MetaField;

public class ObjectIntercept extends MetaObjectIntercept {

    @Override
    public String deleteBefore(AopContext ac) throws Exception {
        Integer id = ac.record.getInt("id");
        // 删除对象关联元字段属性
        MetaField.dao.deleteByObjectId(id);
        // 删除对象关联的所有字典 慎重，会导致误删同表字段
        // String ds = ac.record.getStr("data_source");
        // String table = ac.record.getStr("table_name");
        // String dict = EovaConfig.getProps().get("main_dict_table");
        // Db.use(ds).update("delete from " + dict + " where object = ?", table);

        return null;
    }

}