/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.copyright;

import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;

/**
 * <pre>
 * 用户授权检查(检查为本地License校验,如果屏蔽检查会变成盗版系统,盗版有风险,请使用正版系统)
 *
 * 注意：EOVA 受国家计算机软件著作权保护（登记号：2018SR1012969），不得分享传播源码、二次转售、组团购买等，违者必究。
 * <<计算机软件保护条例>>
 * 第二十四条　未经软件著作权人许可，有下列侵权行为的...依法追究刑事责任：
 * （一）复制或者部分复制著作权人的软件的；
 * （二）向公众发行、出租、通过信息网络传播著作权人的软件的；
 * （三）故意避开或者破坏著作权人为保护其软件著作权而采取的技术措施的；
 * （四）故意删除或者改变软件权利管理电子信息的；
 * （五）转让或者许可他人行使著作权人的软件著作权的。
 * 有前款第一项或者第二项行为的，可以并处每件100元或者货值金额1倍以上5倍以下的罚款；有前款第三项、第四项或者第五项行为的，可以并处20万元以下的罚款。
 *
 * 竭诚为您提供最好的技术服务!
 * 共建良性生态,合作长期共赢!
 * 需要服务请联系:admin@eova.cn
 * </pre>
 *
 * @author Jieven
 **/
public class CopyrightController extends Controller {

    public void index() {

        EovaProApp app = EovaAuth.getEovaApp();

        Ret t = new Ret();

        if (app.isAuth()) {
            t.setOk();
            t.set("appid", app.getId());
            t.set("name", app.getName());
            t.set("token", app.getToken());
        } else {
            t.setFail();
        }

        renderJson(t);
    }
}