/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.template.single;

import com.eova.aop.AopContext;

/**
 * 单表模版业务拦截器<br>
 * 前置任务和后置任务(事务未提交)<br>
 * 成功之后(事务已提交)<br>
 *
 * @author Jieven
 * @date 2014-8-29
 */
public class SingleIntercept {

    /**
     * 导入前置任务(事务内)
     */
    public void importBefore(AopContext ac) throws Exception {
    }

    /**
     * 导入后置任务(事务内)
     */
    public void importAfter(AopContext ac) throws Exception {
    }

    /**
     * 导入成功之后(事务外)
     */
    public void importSucceed(AopContext ac) throws Exception {
    }
}