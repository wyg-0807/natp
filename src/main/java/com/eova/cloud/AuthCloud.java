/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.cloud;

import com.eova.cloud.auth.EovaApp;
import com.eova.common.utils.string.Base64;
import com.eova.common.utils.string.RSAEncrypt;
import com.eova.common.utils.xx;
import com.jfinal.kit.LogKit;

import java.nio.charset.StandardCharsets;

public class AuthCloud {
    public static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCsH+a21jY5whyVZYJmsiIZwWW8efAYJH9nEHRqtJI5qsu5SkZqDOwGBu24ZZA1aRSo/yld1VgTOJK+fgStIcSYf+tnmyZfwhoxjUhQSnL+w60mYAq2sRypCPtHsZsGI4uUmWJhnpuj0CJ/VHEHAyztpHmXWu8abRpIzkK9vv9ivQIDAQAB";

    private static EovaApp app;

    public static boolean isAuthApp(String appId, String appSecret) {
        return getEovaApp().isAuth();
    }

    public static EovaApp getEovaApp() {
        try {
            if (app != null) {
                return app;
            }

            String appId = xx.getConfig("app_id").trim();
            String appSecret = xx.getConfig("app_secret").trim();

            byte[] res = RSAEncrypt.decrypt(RSAEncrypt.loadPublicKeyByStr(publicKey), Base64.decode(appSecret));
            String s = new String(res, StandardCharsets.UTF_8);
            String[] ss = s.split(",");

            app = new EovaApp();
            app.setId(appId);
            app.setSecret(appSecret.replace("©", "&copy;"));
            app.setDomain(xx.getConfig("app_domain"));
            app.setLogo(xx.getConfig("app_logo"));
            app.setName(ss[1]);
            app.setCopyright(ss[2]);
            if (appId.equalsIgnoreCase(ss[0])) {
                app.setAuth(true);
            }

            return app;
        } catch (Exception e) {
            LogKit.info("应用ID和应用密钥在http://www.eova.cn/app 免费注册获取,Eova app config error：" + e.getMessage());
            return null;
        }
    }

}