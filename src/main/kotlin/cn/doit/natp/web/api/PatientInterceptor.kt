package cn.doit.natp.web.api

import cn.doit.natp.common.Strings
import cn.doit.natp.model.Patient
import cn.doit.natp.model.WxSession
import com.jfinal.aop.Interceptor
import com.jfinal.aop.Invocation
import com.jfinal.core.Controller
import com.jfinal.kit.Ret

/**
 * 依赖 cn.doit.natp.web.api.WxSessionInterceptor
 */
class PatientInterceptor : Interceptor {

    override fun intercept(inv: Invocation) {
        val controller: Controller = inv.controller
        val wxSession: WxSession = controller.getWxSession()
        val patient: Patient? = Patient.findActiveBySession(wxSession, true)
                ?: return controller.renderJson(Ret.fail(Strings.MSG, "请先绑定就诊卡").set("hasCard", "0"))

        controller.setAttr(Strings.CURRENT_PATIENT, patient)
        inv.invoke()
    }

}

fun Controller.getCurrentPatient(): Patient = getAttr<Patient>(Strings.CURRENT_PATIENT)