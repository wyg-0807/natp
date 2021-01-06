/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.core.dict;

import com.eova.common.Easy;
import com.eova.common.base.BaseController;
import com.eova.common.utils.xx;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 字典管理
 *
 * @author Jieven
 */
public class DictController extends BaseController {

    // 复制字典
    public void copy() {
        String id = getSelectValue("id");

        String dictTable = xx.getConfig("main_dict_table");

        Record r = Db.use(xx.DS_MAIN).findById(dictTable, id);
        r.remove("id");
        Db.use(xx.DS_MAIN).save(dictTable, r);

        renderJson(new Easy());
    }
}