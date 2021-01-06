package cn.doit.natp.web.swagger

import com.jfinal.core.Controller
import com.jfinal.kit.JsonKit

class SwaggerController : Controller() {

    fun index() {
        render("/swagger/index.html")
    }

    fun api() {
        renderJson(JsonKit.toJson(SwaggerPlugin.getDoc()).replace("\"defaultValue\"".toRegex(), "\"default\""))
    }

}