package cn.doit.natp.web.api

import cn.doit.natp.common.ExceptionInterceptor
import cn.doit.natp.web.open.WxMpController
import com.jfinal.config.Routes

class ApiRoutes : Routes() {

    override fun config() {
        addInterceptor(ExceptionInterceptor())
        addInterceptor(WxSessionInterceptor())
        addInterceptor(PatientInterceptor())

        add("/api/patient", PatientController::class.java)
        add("/api/wxmp", WxMpController::class.java)

        //测试
        add("/api/test",TestController::class.java)
    }

}