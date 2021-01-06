package cn.doit.natp.web.nat

import cn.doit.zhangyi.web.interceptor.RestCacheInterceptor
import cn.doit.zhangyi.web.interceptor.RestInterceptor
import cn.doit.natp.intercept.NatCollectorIntercept
import cn.doit.natp.web.api.WxSessionInterceptor
import com.jfinal.config.Routes

class NatRoutes : Routes() {

    override fun config() {
        addInterceptor(RestCacheInterceptor())
        addInterceptor(RestInterceptor())

        addInterceptor(WxSessionInterceptor())

        addInterceptor(NatCollectorIntercept())

        add("/nat/patient", NatPatientController::class.java)
        add("/nat/collector", NatCollectorController::class.java)
        add("/nat/collect", NatExamCollectController::class.java)

    }

}