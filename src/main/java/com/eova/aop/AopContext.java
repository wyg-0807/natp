/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.aop;

import com.eova.config.EovaConst;
import com.eova.model.MetaObject;
import com.eova.model.User;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;


/**
 * AOP 上下文
 *
 * @author Jieven
 * @date 2014-8-29
 */
public class AopContext {

    /**
     * 当前控制器
     */
    public Controller ctrl;

    /**
     * 当前用户对象
     */
    public User user;

    /**
     * 当前元对象(object.fields=元字段集合)
     */
    public MetaObject object;

    /**
     * 当前操作数据集(批量操作)
     */
    public List<Record> records;

    /**
     * 当前操作对象(单条数据操作)
     */
    public Record record;

    /**
     * 当前操作对象固定值
     * 用途：新增/编辑时预设固定初始值
     * 推荐：固定初始值，建议禁用字段使用addBefore()拦截添加值
     */
    public Record fixed;

    /**
     * 追加SQL条件
     */
    public String condition = "";
    /**
     * 自定义SQL覆盖默认查询条件
     * 格式: where xxx = xxx
     */
    public String where;
    /**
     * 自定义SQL参数
     */
    public List<Object> params = new ArrayList<Object>();
    /**
     * 自定义SQL覆盖默认排序
     * 格式: order by xxx desc
     */
    public String sort;
    /**
     * 完全自定义整个SQL语句(可以支持任意语法,多层嵌套,多表连接查询等)
     */
    public String sql;

    public AopContext(Controller ctrl) {
        this.ctrl = ctrl;
        this.user = ctrl.getSessionAttr(EovaConst.USER);
    }

    public AopContext(Controller ctrl, List<Record> records) {
        this(ctrl);
        this.records = records;
    }

    public AopContext(Controller ctrl, Record record) {
        this(ctrl);
        this.record = record;
    }

    public int UID() {
        return this.user.get("id");
    }

}