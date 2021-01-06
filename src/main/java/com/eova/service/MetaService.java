/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.service;

import com.eova.common.base.BaseService;
import com.eova.model.MetaField;
import com.eova.model.MetaObject;

import java.util.List;

/**
 * 元数据服务
 *
 * @author Jieven
 * @date 2013-1-3
 */
public class MetaService extends BaseService {

    /**
     * 获取元数据(对象和字段)
     *
     * @param objectCode
     * @return
     */
    public MetaObject getMeta(String objectCode) {
        MetaObject object = MetaObject.dao.getByCode(objectCode);
        List<MetaField> fields = MetaField.dao.queryByObjectCode(objectCode);
        object.setFields(fields);
        return object;
    }

}