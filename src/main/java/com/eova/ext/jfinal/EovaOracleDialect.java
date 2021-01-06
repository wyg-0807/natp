/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.ext.jfinal;

import com.jfinal.plugin.activerecord.dialect.OracleDialect;

/**
 * 拓展Oracle方言:个性化识别Number类型和Boolean类型
 *
 * @author Jieven
 */
public class EovaOracleDialect extends OracleDialect {

    public EovaOracleDialect() {
        this.modelBuilder = OracleModelBuilder.me;
        this.recordBuilder = OracleRecordBuilder.me;
    }

}