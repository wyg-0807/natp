/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.ext.jfinal;

import com.eova.common.utils.xx;
import com.eova.config.EovaConst;
import com.jfinal.plugin.activerecord.DbPro;
import com.jfinal.plugin.activerecord.Record;

/**
 * 拓展默认Db操作<br>
 * 1.常用聚合API自动类型转换,使用更顺滑<br>
 * 2.Eova Oracle序列的自动指定
 *
 * @author Jieven
 */
public class EovaDbPro extends DbPro {

    public EovaDbPro(String configName) {
        super(configName);
    }

    @Override
    public boolean save(String tableName, String primaryKey, Record record) {
        // Oracle && 单主键 && 主键没值 -> 指定Sequence
        if (xx.isOracle() && !primaryKey.contains(",") && record.get(primaryKey) == null) {
            record.set(primaryKey, EovaConst.SEQ_ + tableName + ".nextval");
        }
        return super.save(tableName, primaryKey, record);
    }

}