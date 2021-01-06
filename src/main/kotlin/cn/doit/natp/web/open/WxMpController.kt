@file:Suppress("DuplicatedCode")

package cn.doit.natp.web.open

import cn.doit.natp.common.Strings
import cn.doit.natp.model.WxSession
import cn.doit.natp.service.WxServices
import cn.doit.natp.web.api.PatientInterceptor
import cn.doit.natp.web.api.WxSessionInterceptor
import cn.hutool.core.util.IdUtil
import com.eova.common.utils.xx
import com.jfinal.aop.Before
import com.jfinal.aop.Clear
import com.jfinal.core.Controller
import com.jfinal.core.paragetter.Para
import com.jfinal.ext.interceptor.POST
import com.jfinal.kit.Ret
import com.jfinal.kit.StrKit
import com.jfinal.plugin.activerecord.Db
import me.chanjar.weixin.common.error.WxErrorException
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken

/**
 * 微信公众号的登陆、用户信息的展示接口
 */
class WxMpController : Controller() {

    private val wxMpService = WxServices.wxMpService

    /**
     * 验证接口
     */
    @Before(POST::class)
    @Clear(value = [WxSessionInterceptor::class, PatientInterceptor::class])
    fun portal(@Para("signature") signature: String,
               @Para("timestamp") timestamp: String,
               @Para("nonce") nonce: String,
               @Para("echostr") echostr: String) {
        if (wxMpService.checkSignature(timestamp, nonce, signature)) {
            renderJson(echostr)
        }
        renderJson(Ret.fail("msg", "非法请求!"))
    }

    /**
     * 公众号授权回调页面
     */
    @Before(POST::class)
    @Clear(value = [WxSessionInterceptor::class, PatientInterceptor::class])
    fun callRedirect(@Para("code") code: String) {
        try {
            val accessToken = wxMpService.oauth2getAccessToken(code)
            val sid = StrKit.getRandomUUID()
            check(WxSession.createWxMpSession(sid, accessToken)) { "创建会话失败" }

            response.setHeader(Strings.AUTHORIZATION, sid)
            renderJson(Ret.ok(Strings.OBJ, mapOf(Strings.AUTHORIZATION to sid)))
        } catch (e: WxErrorException) {
            renderJson(Ret.fail("msg", e.message))
        }
    }

    fun auth1(@Para("redirect_url") redirectUrl: String) {
        val url: String = xx.getConfig("app_domain") + "/open/wxmp/auth2?redirect_url=$redirectUrl"
        redirect(WxServices.getMpOAuth2AuthorizationUrl(url))
    }

    fun auth2(@Para("redirect_url") redirectUrl: String, @Para("code") code: String) {
        val wxMpOAuth2AccessToken: WxMpOAuth2AccessToken = WxServices.getMpOAuth2AccessToken(code)
                ?: return renderHtml("微信网页授权失败")
        redirect(redirectUrl + "?openid=" + wxMpOAuth2AccessToken.openId)
    }

    fun mpAuth1(@Para("redirect_url") redirectUrl: String) {
        val url: String = xx.getConfig("app_domain") + "/open/wxmp/mpAuth2?redirect_url=$redirectUrl"
        redirect(WxServices.getMpOAuth2AuthorizationUrl(url, "snsapi_userinfo"))
    }

    fun mpAuth2(@Para("redirect_url") redirectUrl: String, @Para("code") code: String) {
        val wxMpOAuth2AccessToken: WxMpOAuth2AccessToken = WxServices.getMpOAuth2AccessToken(code)
                ?: return renderHtml("微信网页授权失败")
        val sid: String = IdUtil.fastSimpleUUID()
        check(WxSession.createWxMpSession(sid, wxMpOAuth2AccessToken)) { "创建会话失败" }
        response.setHeader(Strings.AUTHORIZATION, sid)
        // 如果用户的 mpOpenid 为空，则进行更新
        Db.use(Strings.DS_MAIN).update("UPDATE patient p SET p.mpOpenid=? WHERE p.mpOpenid='' AND p.unionid=?",
                wxMpOAuth2AccessToken.openId,
                wxMpOAuth2AccessToken.unionId)
        redirect("$redirectUrl?sid=$sid")
    }

    fun getJsapiSignature(@Para("url") url: String) {
        val res = WxServices.wxMpService.createJsapiSignature(url)
        renderJson(Ret.ok().set(Strings.OBJ, res))
    }

}