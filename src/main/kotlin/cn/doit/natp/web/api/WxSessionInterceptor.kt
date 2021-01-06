package cn.doit.natp.web.api

import cn.doit.natp.common.Strings
import cn.doit.natp.model.WxSession
import com.jfinal.aop.Interceptor
import com.jfinal.aop.Invocation
import com.jfinal.core.Controller
import com.jfinal.kit.Ret

/**
 * @author lzhq
 */
class WxSessionInterceptor : Interceptor {

    override fun intercept(inv: Invocation) {
        val controller = inv.controller

        val sid = controller.getHeader(Strings.AUTHORIZATION)
                ?: return controller.renderJson(Ret.fail(Strings.MSG, "missing token"))

        val wxSession = WxSession.findOneBySid(sid)
                ?: return controller.renderJson(Ret.fail(Strings.MSG, "invalid token"))

        val openid = wxSession.openid
        controller.setAttr(Strings.WX_SESSION, wxSession)
        controller.setAttr(Strings.OPENID, openid)
        controller.setAttr(Strings.MP_OPENID, wxSession.mpOpenid)
        controller.setAttr(Strings.UNIONID, wxSession.unionid)

        inv.invoke()
    }

}

/**
 * 当前用户的 wxSession
 */
fun Controller.getWxSession(): WxSession = getAttr(Strings.WX_SESSION)
fun Controller.getMpopenid(): String = getAttr<String>(Strings.MP_OPENID)
