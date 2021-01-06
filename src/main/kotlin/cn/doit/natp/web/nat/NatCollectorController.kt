package cn.doit.natp.web.nat

import cn.doit.zhangyi.common.kit.Values
import cn.doit.natp.intercept.NatCollectorIntercept
import cn.doit.natp.model.NatCollector
import cn.doit.natp.model.WxSession
import cn.doit.natp.web.api.WxSessionInterceptor
import cn.doit.natp.web.api.getMpopenid
import cn.hutool.core.util.IdUtil
import com.jfinal.aop.Before
import com.jfinal.aop.Clear
import com.jfinal.core.Controller
import com.jfinal.core.paragetter.Para
import com.jfinal.ext.interceptor.GET
import com.jfinal.ext.interceptor.POST
import com.jfinal.kit.Ret


class NatCollectorController : Controller() {

    companion object {
        private val RET_UNAUTHENTICATED = Ret.create("state", "ok").set(Values.MSG, "notLogged")

        private val RET_LOGIN_INVALID = Ret.fail().set(Values.MSG, "工号或密码不正确！")
    }

    @Before(GET::class)
    fun getUserInfo(): Any {
        val openid = getMpopenid()
        val natCollector = NatCollector.findByOpenid(openid) ?: return RET_UNAUTHENTICATED
        natCollector.desensitization()
        return natCollector
    }

    @Before(POST::class)
    @Clear(NatCollectorIntercept::class, WxSessionInterceptor::class)
    fun login(@Para("employeeNumber") employeeNumber: String, @Para("loginPassword") loginPassword: String): Any {
        val sid: String = IdUtil.fastSimpleUUID()
        check(WxSession.createPcSession(sid, employeeNumber)) { "创建会话失败" }
//        val openid = getMpopenid()
        val natCollector = NatCollector.findByEmployeeNumber(employeeNumber) ?: return RET_LOGIN_INVALID
        if (natCollector.loginPassword != loginPassword) return RET_LOGIN_INVALID
        // 先注销当前已登陆的采集者
        /*natCollector.openid?.let {
            natCollector.openid = null
            natCollector.update()
        }*/
        //使用sid作为唯一校验
        natCollector.openid = sid
        natCollector.update()
        natCollector.desensitization()
        return natCollector.put("sid", sid)
    }

    @Before(POST::class)
    fun uploadPwd(
            @Para("oldPwd") oldPwd: String,
            @Para("newPwd") newPwd: String,
            @Para("confirm") confirm: String): Any {
        val openid = getMpopenid()
        val natCollector = NatCollector.findByOpenid(openid) ?: return RET_UNAUTHENTICATED
        if (confirm != newPwd) return "确认密码不一致！！"
        if (natCollector.loginPassword != oldPwd) return "不正确的旧密码！"
        if (confirm.length < 6) return "新密码长度过短！"
        natCollector.loginPassword = confirm
        natCollector.update()
        return natCollector
    }

}