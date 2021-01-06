package cn.doit.natp.web.nat

import cn.doit.zhangyi.common.kit.Strings
import cn.doit.natp.intercept.NatCollectorIntercept
import cn.doit.natp.model.NatPatient
import cn.doit.natp.model.saveOrUpdate
import cn.doit.natp.web.api.WxSessionInterceptor
import cn.doit.natp.web.api.getMpopenid
import com.jfinal.aop.Before
import com.jfinal.aop.Clear
import com.jfinal.core.Controller
import com.jfinal.core.paragetter.Para
import com.jfinal.ext.interceptor.GET
import com.jfinal.ext.interceptor.POST


@Clear(NatCollectorIntercept::class)
class NatPatientController : Controller() {

    @Before(GET::class)
    fun listPatient(): Any {
        val openid = getMpopenid()
        val patientList = NatPatient.listByOpenid(openid)
        return patientList
    }

    @Clear(WxSessionInterceptor::class)
    @Before(GET::class)
    fun qrCode(content: String) {
        renderQrCode("ZHANGYI:$content", 192, 192, 'H')
    }

    @Before(POST::class)
    fun saveOrUpdate(@Para("") patient: NatPatient): Boolean {
        val openid = getMpopenid()
        // 校验数据
        checkNotNull(patient.getStr("idCardNo")) { "身份证号为空！" }
        checkNotNull(patient.getStr("name")) { "姓名为空！" }
        checkNotNull(patient.getStr("mobile")) { "联系方式为空！" }
        checkNotNull(patient.getStr("address")) { "居住地址信息为空！" }
        checkNotNull(patient.getStr("cardType")) { "证件类别信息为空！" }
        check(patient.mobile.length in 7..11) { "联系方式长度范围7~11！" }
        check(Strings.validateName(patient.name)) { "不合法的姓名！" }
        patient.department = patient.department
        patient.openid = openid
        return patient.saveOrUpdate()
    }

    @Before(POST::class)
    fun remove(@Para("id") id: Int): Any {
        val openid = getMpopenid()
        // 校验数据
        val patient = NatPatient.DAO.findById(id)
        if (openid != patient.openid) return "您没有相关权限"
        if (patient.delete()) return patient
        return "操作失败"
    }

}