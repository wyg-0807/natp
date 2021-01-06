/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.copyright;

import com.eova.common.utils.string.Base64;
import com.eova.common.utils.string.RSAEncrypt;
import com.eova.config.EovaConfig;
import com.jfinal.kit.LogKit;

import java.nio.charset.StandardCharsets;

/**
 * 专业版授权校验
 *
 * @author Jieven
 */
public class EovaAuth {

    public static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCo16QFj8HSQA3rrjqDDm4ZMOuMJGgP3y8cuSoKoNA/OzAF54qmAzAS+ewaEsz/mobo4KJq6mbW0oNv+aD1BYwHWlfv9XP3/QsanRT/A0NauH2ALsU/R2SU1UZk8IyyVauK4tOIwtQW4IWn761OZdOAAO4LFueDJiKNAIFnWkc7gQIDAQAB";

    private static EovaProApp app;

    public static boolean isAuthApp(String appId, String appSecret) {
        return getEovaApp().isAuth();
    }

    public static EovaProApp getEovaApp() {
        try {
            if (app != null) {
                return app;
            }

            String appId = EovaConfig.APP_ID;
            String appSecret = EovaConfig.APP_SECRET;
            /**
             * 如果你想屏蔽授权检查,来达到破解的目的,你可能面临4个风险:
             * 1.法律风险:<<计算机软件保护条例>>（三）故意避开或者破坏著作权人为保护其软件著作权而采取的技术措施的；
             * 2.技术风险:前端依赖此处解密过程,如果你屏蔽解密,前端有可能中风.
             * 3.时间风险:我们知道,无论怎么防护,都有办法破解,所以未来我们要免费,所以你可能白折腾了.
             * 4.服务风险:代码本身是没有价值的,所以我们会花更多精力在服务上.
             */
            byte[] res = RSAEncrypt.decrypt(RSAEncrypt.loadPublicKeyByStr(publicKey), Base64.decode(appSecret));
            String s = new String(res, StandardCharsets.UTF_8);
            String[] ss = s.split(",");

            app = new EovaProApp();
            app.setId(appId);
            app.setName(ss[1]);
            app.setToken(ss[2]);
            if (appId.equalsIgnoreCase(ss[0])) {
                app.setAuth(true);
            }

            return app;
        } catch (Exception e) {
            LogKit.info("专业版应用ID和应用密钥在http://www.eova.cn 免费注册获取,Eova app config error：" + e.getMessage());
            return null;
        }
    }

}