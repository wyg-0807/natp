/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.aop.eova;

/**
 * <pre>
 * 全局Eova业务拦截器
 * </pre>
 *
 * @author Jieven
 */
public class EovaIntercept {

    /**
     * <pre>
     * 过滤查询数据
     *
     * ac.ctrl 当前控制器
     * ac.user 当前用户
     * ac.menu 当前菜单
     * ac.object 当前元对象
     * ec.object.getFields() 当前元字段
     * </pre>
     *
     * @param ac
     */
    public String filterQuery(EovaContext ec) throws Exception {
        return "";
    }

    /**
     * <pre>
     * 过滤表达式数据
     *
     * ac.ctrl 当前控制器
     * ac.user 当前用户
     * ac.exp 当前表达式
     * </pre>
     *
     * @param ac
     */
    public String filterExp(EovaContext ec) throws Exception {
        return "";
    }

}