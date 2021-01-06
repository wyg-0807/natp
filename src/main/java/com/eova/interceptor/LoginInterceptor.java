/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.interceptor;

import com.eova.common.utils.util.AntPathMatcher;
import com.eova.common.utils.xx;
import com.eova.config.EovaConst;
import com.eova.i18n.I18NBuilder;
import com.eova.model.User;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

import java.util.ArrayList;

/**
 * 常量加载拦截器
 *
 * @author Jieven
 */
public class LoginInterceptor implements Interceptor {

    /**
     * 登录拦截排除URI<br>
     * ?  匹配任何单字符<br>
     * *  匹配0或者任意数量的字符<br>
     * ** 匹配0或者更多的目录 <br>
     */
    public static ArrayList<String> excludes = new ArrayList<String>();

    static {
        excludes.add("/captcha");
        excludes.add("/toLogin");
        excludes.add("/vcodeImg");
        excludes.add("/doLogin");
        excludes.add("/doInit");
        excludes.add("/toTest");
        excludes.add("/form");
        excludes.add("/doForm");
        excludes.add("/upgrade");
        excludes.add("/why");
    }

    @Override
    public void intercept(Invocation inv) {

        sessionInterceptor(inv);

        String uri = inv.getActionKey();

        AntPathMatcher pm = new AntPathMatcher();
        for (String pattern : excludes) {
            if (pm.match(pattern, uri)) {
                inv.invoke();
                return;
            }
        }

        // 获取登录用户的角色
        User user = inv.getController().getSessionAttr(EovaConst.USER);
        if (user == null) {
            inv.getController().redirect("/toLogin");
            return;
        }

        inv.invoke();
    }

    /**
     * 当前会话拦截器管理
     * PS:服务器网络模型不同导致新建线程策略不同,每次请求都会被一个线程处理,但是可能并不是一个新线程,比如线程池.
     *
     * @param inv
     */
    public void sessionInterceptor(Invocation inv) {
        String local = inv.getController().getSessionAttr(EovaConst.LOCAL);
        if (!xx.isEmpty(local)) {
            I18NBuilder.setLocal(local);
        }
    }
}