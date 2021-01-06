package cn.doit.natp.web.open

import com.jfinal.config.Routes

class OpenRoutes : Routes() {
    override fun config() {
        add("/open/wxmp", WxMpController::class.java)
        add("/open/dicts", DictController::class.java)
    }
}