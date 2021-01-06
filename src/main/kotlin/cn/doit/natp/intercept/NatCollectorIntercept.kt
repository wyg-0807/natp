package cn.doit.natp.intercept

import cn.doit.zhangyi.common.kit.Values
import cn.doit.natp.common.Strings
import cn.doit.natp.model.NatCollector
import com.jfinal.aop.Interceptor
import com.jfinal.aop.Invocation
import com.jfinal.core.Controller
import com.jfinal.kit.Ret

class NatCollectorIntercept : Interceptor {

    companion object {
        private val RET_UNAUTHENTICATED = Ret.create("state", "ok").set(Values.MSG, "notLogged")
    }

    override fun intercept(inv: Invocation) {
        val controller: Controller = inv.controller
        val openid: String = controller.getHeader(Strings.AUTHORIZATION)
        val natCollector = NatCollector.findByOpenid(openid) ?: return controller.renderJson(RET_UNAUTHENTICATED)
        controller.setAttr("CURRENT_COLLECTOR", natCollector)
        inv.invoke()
    }

}

fun Controller.getCurrentCollector(): NatCollector = getAttr("CURRENT_COLLECTOR")