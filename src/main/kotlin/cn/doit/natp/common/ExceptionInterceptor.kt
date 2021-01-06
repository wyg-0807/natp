package cn.doit.natp.common

import cn.hutool.core.exceptions.ExceptionUtil
import com.jfinal.aop.Interceptor
import com.jfinal.aop.Invocation
import com.jfinal.kit.LogKit
import com.jfinal.kit.Ret

class ExceptionInterceptor : Interceptor {

    override fun intercept(inv: Invocation) {
        try {
            inv.invoke()
        } catch (e: Exception) {
            LogKit.debug(e.message, e)
            inv.controller.renderJson(Ret.fail(Strings.MSG, ExceptionUtil.stacktraceToOneLineString(e, 200)))
        }
    }

}