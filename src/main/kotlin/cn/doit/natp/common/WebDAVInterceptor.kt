package cn.doit.natp.common

import com.jfinal.aop.Interceptor
import com.jfinal.aop.Invocation

/**
 * 仅允许 GET 和 POST 方法的拦截器，其他 HTTP 请求方法则返回 405 错误。
 */
class WebDAVInterceptor : Interceptor {

    override fun intercept(inv: Invocation) {
        val method = inv.controller.request.method
        if (method.toUpperCase() in arrayListOf("GET", "POST")) {
            inv.invoke()
        } else {
            inv.controller.renderError(405)
        }
    }

}